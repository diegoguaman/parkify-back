package com.igrowker.feature.parkify.features.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public record RegisterRequest(
        String username,

        @Schema(description = "User's email", example = "user@example.com")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "User's password", example = "password123")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password,

        @Pattern(regexp = "^(DRIVER|OWNER)$", message = "Role must be either DRIVER or OWNER")
        String role,

        String contactPhone
) {
}
