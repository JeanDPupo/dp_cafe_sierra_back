package edu.unimagdalena.cowork.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class ProducerProfileDtos {

    private ProducerProfileDtos() {
    }

    public record UpsertProducerProfileRequest(
            boolean activeSeller,
            @Size(max = 120) String brandName,
            @Size(max = 500) String bio,
            @Size(max = 1500) String story,
            @Size(max = 180) String locationText,
            @Size(max = 80) String gps,
            @Size(max = 80) String yearsExperience,
            @Size(max = 500) String coverImageUrl,
            @Size(max = 500) String paymentDetails,
            @Size(max = 300) String mercadopagoAccessToken,
            @Size(max = 300) String mercadopagoPublicKey,
            @Size(max = 20) String nequiPhone
    ) {
    }

    public record FarmRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(max = 180) String locationText,
            @Size(max = 80) String gps,
            @Size(max = 800) String description,
            boolean active
    ) {
    }

    public record FarmResponse(
            Long id,
            String name,
            String locationText,
            String gps,
            String description,
            boolean active,
            Instant createdAt
    ) {
    }

    public record ProducerProfileResponse(
            Long id,
            Long userId,
            String ownerName,
            String phone,
            String whatsappNumber,
            boolean activeSeller,
            String brandName,
            String bio,
            String story,
            String locationText,
            String gps,
            String yearsExperience,
            String coverImageUrl,
            String paymentDetails,
            String mercadopagoAccessToken,
            String mercadopagoPublicKey,
            String nequiPhone,
            Instant createdAt,
            List<FarmResponse> farms,
            List<ProductDtos.ProductCatalogItemResponse> products
    ) {
    }
}
