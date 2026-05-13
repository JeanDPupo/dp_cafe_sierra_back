package edu.unimagdalena.cowork.api.dto;

import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class UserDtos {

    private UserDtos() {
    }

    public record UpdateUserRequest(
            @Size(max = 120) String fullName,
            @Size(max = 20) String phone,
            @Size(max = 20) String whatsappNumber
    ) {
    }

    public record UserResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            String whatsappNumber,
            String role,
            Instant createdAt
    ) {
    }
}
