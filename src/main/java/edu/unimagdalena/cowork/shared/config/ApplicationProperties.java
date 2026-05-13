package edu.unimagdalena.cowork.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ApplicationProperties(
        Jwt jwt,
        Payment payment,
        String frontendUrl
) {

    public record Jwt(String secret, long expirationMs) {
    }

    public record Payment(MercadoPago mercadopago) {
    }

    public record MercadoPago(String accessToken, String webhookSecret) {
    }
}
