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
            @NotBlank @Size(max = 120) String brandName,
            @NotBlank @Size(max = 120) String farmName,
            @Size(max = 500) String bio,
            @Size(max = 1500) String story,
            @NotBlank @Size(max = 180) String locationText,
            @Size(max = 80) String gps,
            @Size(max = 80) String yearsExperience,
            @Size(max = 500) String coverImageUrl
    ) {
    }

    public record ProducerProfileResponse(
            Long id,
            Long userId,
            String ownerName,
            boolean activeSeller,
            String brandName,
            String farmName,
            String bio,
            String story,
            String locationText,
            String gps,
            String yearsExperience,
            String coverImageUrl,
            Instant createdAt,
            List<ProductDtos.ProductCatalogItemResponse> products
    ) {
    }
}
