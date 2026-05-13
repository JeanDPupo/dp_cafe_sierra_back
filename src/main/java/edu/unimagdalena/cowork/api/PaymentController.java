package edu.unimagdalena.cowork.api;

import edu.unimagdalena.cowork.api.dto.PaymentDtos;
import edu.unimagdalena.cowork.domain.services.PaymentService;
import edu.unimagdalena.cowork.shared.security.SecurityUtils;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/preference")
    public PaymentDtos.PaymentPreferenceResponse createPreference(@PathVariable Long orderId) {
        return paymentService.createPreference(SecurityUtils.currentUserId(), orderId);
    }

    @PostMapping("/webhooks/mercadopago")
    public void webhook(@RequestBody Map<String, Object> payload) {
        paymentService.processWebhook(payload);
    }

    @GetMapping("/{id}")
    public PaymentDtos.PaymentResponse getById(@PathVariable Long id) {
        return paymentService.getById(id);
    }
}
