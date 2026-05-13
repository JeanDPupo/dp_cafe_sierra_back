package edu.unimagdalena.cowork.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {

    private OrderDtos() {
    }

    public record OrderItemResponse(
            Long id,
            Long productId,
            String productName,
            BigDecimal quantityKg,
            BigDecimal unitPriceSnapshot,
            BigDecimal subtotal
    ) {
    }

    public record OrderResponse(
            Long id,
            Long buyerUserId,
            Long sellerProfileId,
            String sellerBrandName,
            BigDecimal totalAmount,
            String status,
            String paymentStatus,
            Instant createdAt,
            List<OrderItemResponse> items
    ) {
    }
}
