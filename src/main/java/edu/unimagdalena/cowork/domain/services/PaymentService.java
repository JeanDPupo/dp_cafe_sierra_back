package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.PaymentDtos;
import edu.unimagdalena.cowork.domain.entities.Order;
import edu.unimagdalena.cowork.domain.entities.Payment;
import edu.unimagdalena.cowork.domain.entities.PaymentEvent;
import edu.unimagdalena.cowork.domain.entities.PaymentProvider;
import edu.unimagdalena.cowork.domain.entities.PaymentStatus;
import edu.unimagdalena.cowork.domain.exception.BadRequestException;
import edu.unimagdalena.cowork.domain.exception.ForbiddenOperationException;
import edu.unimagdalena.cowork.domain.exception.ResourceNotFoundException;
import edu.unimagdalena.cowork.domain.repositories.PaymentEventRepository;
import edu.unimagdalena.cowork.domain.repositories.PaymentRepository;
import edu.unimagdalena.cowork.shared.config.ApplicationProperties;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final OrderService orderService;
    private final ApplicationProperties properties;
    private final RestTemplate restTemplate;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentEventRepository paymentEventRepository,
            OrderService orderService,
            ApplicationProperties properties,
            RestTemplate restTemplate
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentEventRepository = paymentEventRepository;
        this.orderService = orderService;
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public PaymentDtos.PaymentPreferenceResponse createPreference(Long userId, Long orderId, String providerValue) {
        Order order = orderService.getEntityById(orderId);
        if (!order.getBuyerUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("Solo el comprador puede iniciar el pago");
        }
        PaymentProvider provider = parseProvider(providerValue);
        String externalReference = buildExternalReference(orderId, provider);
        Payment payment = paymentRepository.findByExternalReference(externalReference)
                .orElseGet(() -> {
                    Payment created = new Payment();
                    created.setOrder(order);
                    created.setProvider(provider);
                    created.setExternalReference(externalReference);
                    created.setAmount(order.getTotalAmount());
                    created.setStatus(PaymentStatus.PENDING);
                    return paymentRepository.save(created);
                });

        String checkoutUrl = switch (provider) {
            case MERCADO_PAGO -> createMercadoPagoPreference(order, payment);
            case NEQUI -> createNequiCheckout(order, payment);
        };
        return new PaymentDtos.PaymentPreferenceResponse(
                payment.getId(),
                payment.getExternalReference(),
                payment.getProvider().name(),
                checkoutUrl,
                payment.getStatus().name()
        );
    }

    @Transactional
    public void processWebhook(Map<String, Object> payload, String providerValue) {
        PaymentProvider provider = parseProvider(providerValue);
        String externalReference = payload.getOrDefault("external_reference", "").toString();
        if (externalReference.isBlank()) {
            return;
        }
        Payment payment = paymentRepository.findByExternalReference(externalReference)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        if (payment.getProvider() != provider) {
            throw new BadRequestException("El proveedor del webhook no coincide con el pago");
        }
        String statusValue = payload.getOrDefault("status", PaymentStatus.PENDING.name()).toString().toUpperCase();
        payment.setStatus(parsePaymentStatus(statusValue));
        payment.setProviderPaymentId(payload.getOrDefault("id", "").toString());
        paymentRepository.save(payment);

        payment.getOrder().setPaymentStatus(payment.getStatus());
        if (payment.getStatus() == PaymentStatus.APPROVED) {
            payment.getOrder().setStatus(edu.unimagdalena.cowork.domain.entities.OrderStatus.CONFIRMED);
        }

        PaymentEvent event = new PaymentEvent();
        event.setPayment(payment);
        event.setEventType(payload.getOrDefault("type", "webhook").toString());
        event.setRawPayload(payload.toString());
        paymentEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public PaymentDtos.PaymentResponse getById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        return new PaymentDtos.PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getProvider().name(),
                payment.getExternalReference(),
                payment.getProviderPaymentId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getCreatedAt()
        );
    }

    private String createMercadoPagoPreference(Order order, Payment payment) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(properties.payment().mercadopago().accessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("external_reference", payment.getExternalReference());
            body.put("items", java.util.List.of(Map.of(
                    "title", "Pedido de cafe #" + order.getId(),
                    "quantity", 1,
                    "currency_id", "COP",
                    "unit_price", order.getTotalAmount()
            )));
            body.put("back_urls", Map.of(
                    "success", properties.frontendUrl() + "/?payment=success",
                    "failure", properties.frontendUrl() + "/?payment=failure",
                    "pending", properties.frontendUrl() + "/?payment=pending"
            ));

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.mercadopago.com/checkout/preferences",
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Map.class
            );
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.get("init_point") != null) {
                return responseBody.get("init_point").toString();
            }
        } catch (Exception ignored) {
            // Fallback controlado para no bloquear desarrollo local.
        }
        return properties.frontendUrl() + "/payments/mock/" + UUID.randomUUID();
    }

    private String createNequiCheckout(Order order, Payment payment) {
        return properties.frontendUrl()
                + "/?payment=nequi&orderId="
                + order.getId()
                + "&reference="
                + payment.getExternalReference();
    }

    private PaymentProvider parseProvider(String providerValue) {
        if (providerValue == null || providerValue.isBlank()) {
            return PaymentProvider.MERCADO_PAGO;
        }
        try {
            return PaymentProvider.valueOf(providerValue.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Proveedor de pago no soportado");
        }
    }

    private String buildExternalReference(Long orderId, PaymentProvider provider) {
        return "order-" + orderId + "-" + provider.name().toLowerCase();
    }

    private PaymentStatus parsePaymentStatus(String statusValue) {
        return switch (statusValue) {
            case "APPROVED", "APPROVED_PAYMENT" -> PaymentStatus.APPROVED;
            case "REJECTED" -> PaymentStatus.REJECTED;
            case "CANCELLED" -> PaymentStatus.CANCELLED;
            default -> PaymentStatus.PENDING;
        };
    }
}
