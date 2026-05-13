package edu.unimagdalena.cowork.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 8, max = 120) String password,
            @NotBlank @Size(max = 20) String phone,
            @NotBlank @Size(max = 20) String whatsappNumber
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record AuthResponse(
            String token,
            UserDtos.UserResponse user,
            Instant expiresAt
    ) {
    }
}
