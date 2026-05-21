package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.OrderDtos;
import edu.unimagdalena.cowork.domain.entities.Cart;
import edu.unimagdalena.cowork.domain.entities.CartItem;
import edu.unimagdalena.cowork.domain.entities.CartStatus;
import edu.unimagdalena.cowork.domain.entities.Order;
import edu.unimagdalena.cowork.domain.entities.OrderItem;
import edu.unimagdalena.cowork.domain.entities.OrderStatus;
import edu.unimagdalena.cowork.domain.entities.PaymentStatus;
import edu.unimagdalena.cowork.domain.entities.Product;
import edu.unimagdalena.cowork.domain.entities.ProductStatus;
import edu.unimagdalena.cowork.domain.entities.ProducerProfile;
import edu.unimagdalena.cowork.domain.exception.BadRequestException;
import edu.unimagdalena.cowork.domain.exception.ForbiddenOperationException;
import edu.unimagdalena.cowork.domain.exception.ResourceNotFoundException;
import edu.unimagdalena.cowork.domain.repositories.CartRepository;
import edu.unimagdalena.cowork.domain.repositories.OrderItemRepository;
import edu.unimagdalena.cowork.domain.repositories.OrderRepository;
import edu.unimagdalena.cowork.domain.repositories.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ProducerProfileService producerProfileService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartService cartService,
            CartRepository cartRepository,
            ProductRepository productRepository,
            UserService userService,
            ProducerProfileService producerProfileService
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.producerProfileService = producerProfileService;
    }

    @Transactional
    public OrderDtos.OrderResponse checkout(Long userId) {
        Cart cart = cartService.getOrCreateCart(userId);
        List<CartItem> items = cartService.getItems(cart);
        if (items.isEmpty()) {
            throw new BadRequestException("El carrito esta vacio");
        }
        Set<Long> sellerIds = items.stream()
                .map(item -> item.getProduct().getProducerProfile().getId())
                .collect(Collectors.toSet());
        if (sellerIds.size() != 1) {
            throw new BadRequestException("Por ahora el checkout solo soporta productos de un mismo productor");
        }
        for (CartItem item : items) {
            Product product = item.getProduct();
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new BadRequestException("Uno de los productos ya no esta disponible");
            }
            if (item.getQuantityKg().compareTo(product.getAvailableKg()) > 0) {
                throw new BadRequestException("La cantidad de " + product.getName() + " supera la disponibilidad actual");
            }
        }
        ProducerProfile sellerProfile = items.get(0).getProduct().getProducerProfile();
        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setBuyerUser(userService.getById(userId));
        order.setSellerProfile(sellerProfile);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTotalAmount(total);
        orderRepository.save(order);

        for (CartItem item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantityKg(item.getQuantityKg());
            orderItem.setUnitPriceSnapshot(item.getUnitPriceSnapshot());
            orderItem.setSubtotal(item.getSubtotal());
            orderItemRepository.save(orderItem);

            Product product = item.getProduct();
            product.setAvailableKg(product.getAvailableKg().subtract(item.getQuantityKg()));
            if (product.getAvailableKg().compareTo(BigDecimal.ZERO) == 0) {
                product.setStatus(ProductStatus.SOLD_OUT);
            }
            productRepository.save(product);
        }

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Order getEntityById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));
    }

    @Transactional(readOnly = true)
    public OrderDtos.OrderResponse getOrderForUser(Long userId, Long orderId) {
        Order order = getEntityById(orderId);
        Long sellerUserId = order.getSellerProfile().getUser().getId();
        if (!order.getBuyerUser().getId().equals(userId) && !sellerUserId.equals(userId)) {
            throw new ForbiddenOperationException("No tienes acceso a esta orden");
        }
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDtos.OrderResponse> getPurchases(Long userId) {
        return orderRepository.findByBuyerUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDtos.OrderResponse> getSales(Long userId) {
        ProducerProfile profile = producerProfileService.getByUserId(userId);
        return orderRepository.findBySellerProfileIdOrderByCreatedAtDesc(profile.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderDtos.OrderResponse toResponse(Order order) {
        List<OrderDtos.OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId()).stream()
                .map(item -> new OrderDtos.OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantityKg(),
                        item.getUnitPriceSnapshot(),
                        item.getSubtotal()
                ))
                .toList();
        return new OrderDtos.OrderResponse(
                order.getId(),
                order.getBuyerUser().getId(),
                order.getSellerProfile().getId(),
                order.getSellerProfile().getBrandName(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.getCreatedAt(),
                items
        );
    }
}
