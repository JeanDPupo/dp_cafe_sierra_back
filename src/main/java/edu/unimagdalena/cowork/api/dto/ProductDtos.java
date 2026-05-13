package edu.unimagdalena.cowork.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ProductDtos {

    private ProductDtos() {
    }

    public record ProcessMediaRequest(
            @NotBlank String mediaType,
            @NotBlank @Size(max = 500) String url,
            @Size(max = 180) String caption
    ) {
    }

    public record ProductProcessRequest(
            @NotBlank String stage,
            @NotBlank @Size(max = 1500) String description,
            @NotBlank @Size(max = 120) String resultType,
            @NotNull Integer orderIndex,
            @Valid List<ProcessMediaRequest> media
    ) {
    }

    public record ProductCreateRequest(
            @NotBlank @Size(max = 140) String name,
            @NotBlank @Size(max = 80) String variety,
            @NotNull @DecimalMin("0.01") BigDecimal pricePerKg,
            @NotNull @DecimalMin("0.01") BigDecimal availableKg,
            @NotBlank @Size(max = 1500) String description,
            @Size(max = 500) String mainImageUrl,
            @NotEmpty @Valid List<ProductProcessRequest> processes
    ) {
    }

    public record ProductUpdateRequest(
            @Size(max = 140) String name,
            @Size(max = 80) String variety,
            @DecimalMin("0.01") BigDecimal pricePerKg,
            @DecimalMin("0.00") BigDecimal availableKg,
            @Size(max = 1500) String description,
            @Size(max = 500) String mainImageUrl,
            String status,
            @Valid List<ProductProcessRequest> processes
    ) {
    }

    public record ProductCatalogItemResponse(
            Long id,
            Long producerProfileId,
            String producerBrandName,
            String producerLocation,
            String name,
            String variety,
            BigDecimal pricePerKg,
            BigDecimal availableKg,
            String description,
            String mainImageUrl,
            String status,
            Instant createdAt
    ) {
    }

    public record ProcessMediaResponse(
            Long id,
            String mediaType,
            String url,
            String caption
    ) {
    }

    public record ProductProcessResponse(
            Long id,
            String stage,
            String description,
            String resultType,
            Integer orderIndex,
            List<ProcessMediaResponse> media
    ) {
    }

    public record ProductDetailResponse(
            Long id,
            String name,
            String variety,
            BigDecimal pricePerKg,
            BigDecimal availableKg,
            String description,
            String mainImageUrl,
            String status,
            Instant createdAt,
            ProducerProfileDtos.ProducerProfileResponse producer,
            List<ProductProcessResponse> processes,
            Double averageRating,
            Long commentCount
    ) {
    }
}
