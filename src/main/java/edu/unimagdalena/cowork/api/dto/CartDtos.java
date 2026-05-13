package edu.unimagdalena.cowork.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public final class CartDtos {

    private CartDtos() {
    }

    public record AddCartItemRequest(
            @NotNull Long productId,
            @NotNull @DecimalMin("0.01") BigDecimal quantityKg
    ) {
    }

    public record UpdateCartItemRequest(
            @NotNull @DecimalMin("0.01") BigDecimal quantityKg
    ) {
    }

    public record CartItemResponse(
            Long id,
            Long productId,
            String productName,
            BigDecimal quantityKg,
            BigDecimal unitPriceSnapshot,
            BigDecimal subtotal
    ) {
    }

    public record CartResponse(
            Long id,
            String status,
            List<CartItemResponse> items,
            BigDecimal total
    ) {
    }
}
