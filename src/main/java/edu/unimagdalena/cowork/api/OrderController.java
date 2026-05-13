package edu.unimagdalena.cowork.api;

import edu.unimagdalena.cowork.api.dto.OrderDtos;
import edu.unimagdalena.cowork.domain.services.OrderService;
import edu.unimagdalena.cowork.shared.security.SecurityUtils;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/checkout")
    public OrderDtos.OrderResponse checkout() {
        return orderService.checkout(SecurityUtils.currentUserId());
    }

    @GetMapping("/orders/{id}")
    public OrderDtos.OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrderForUser(SecurityUtils.currentUserId(), id);
    }

    @GetMapping("/users/me/purchases")
    public List<OrderDtos.OrderResponse> purchases() {
        return orderService.getPurchases(SecurityUtils.currentUserId());
    }

    @GetMapping("/users/me/sales")
    public List<OrderDtos.OrderResponse> sales() {
        return orderService.getSales(SecurityUtils.currentUserId());
    }

    @GetMapping("/users/me/sales/{orderId}")
    public OrderDtos.OrderResponse saleDetail(@PathVariable Long orderId) {
        return orderService.getOrderForUser(SecurityUtils.currentUserId(), orderId);
    }
}
