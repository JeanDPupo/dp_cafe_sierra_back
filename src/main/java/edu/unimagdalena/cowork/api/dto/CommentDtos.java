package edu.unimagdalena.cowork.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class CommentDtos {

    private CommentDtos() {
    }

    public record CommentCreateRequest(
            @Min(1) @Max(5) Integer rating,
            @NotBlank @Size(max = 1000) String content
    ) {
    }

    public record CommentUpdateRequest(
            @Min(1) @Max(5) Integer rating,
            @Size(max = 1000) String content,
            Boolean visible
    ) {
    }

    public record CommentResponse(
            Long id,
            Long authorUserId,
            String authorName,
            Integer rating,
            String content,
            boolean visible,
            Instant createdAt
    ) {
    }
}
