package edu.unimagdalena.cowork.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public final class PaymentDtos {

    private PaymentDtos() {
    }

    public record PaymentPreferenceResponse(
            Long paymentId,
            String externalReference,
            String provider,
            String checkoutUrl,
            String status
    ) {
    }

    public record PaymentResponse(
            Long id,
            Long orderId,
            String provider,
            String externalReference,
            String providerPaymentId,
            BigDecimal amount,
            String status,
            Instant createdAt
    ) {
    }
}
