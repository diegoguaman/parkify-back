package com.igrowker.feature.parkify.features.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to update the user's profile information")
public record UpdateUserRequest (

    @Schema(description = "New username", example = "new_name")
    @NotBlank(message = "Username cannot be blank")
    String username,

    @Schema(description = "New email address", example = "test@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    String email,

    @Schema(description = "Updated contact phone number", example = "1122334455")
    @NotBlank(message = "Contact phone cannot be blank")
    String contactPhone,

    @Schema(description = "New latitude for the user location", example = "-34.6037")
    Double latitude,

    @Schema(description = "New longitude for the user location", example = "-58.3816")
    Double longitude
) {}
