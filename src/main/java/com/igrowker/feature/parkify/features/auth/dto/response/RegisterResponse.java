package com.igrowker.feature.parkify.features.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(description = "Response containing user registration details")
@AllArgsConstructor
@SuperBuilder
public class RegisterResponse extends UserResponse {
}
