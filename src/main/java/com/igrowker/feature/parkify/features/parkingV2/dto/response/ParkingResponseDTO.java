package com.igrowker.feature.parkify.features.parkingV2.dto.response;

import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.parkingV2.entities.AccessType;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "DTO for returning parking data")
public record ParkingResponseDTO(
        @Schema(description = "ID of the parking", example = "b8e69c4e-ffbb-4fae-9340-2b65cbb8b94c")
        UUID id,

        @Schema(description = "ID of the owner", example = "123")
        Long ownerId,

        @Schema(description = "Name of the parking lot", example = "Parking Palermo")
        String parkingName,

        @Schema(description = "Address of the parking lot", example = "Av. Libertador 1234")
        String parkingAddress,

        @Schema(description = "Contact phone number", example = "1134567890")
        String parkingPhone,

        @Schema(description = "URL of the parking image", example = "https://example.com/image.jpg")
        String imageUrl,

        @Schema(description = "Total number of spots", example = "20")
        int totalSpots,

        @Schema(description = "Available number of spots", example = "15")
        int availableSpots,

        @Schema(description = "Extra features", example = "[\"covered\", \"24h surveillance\"]")
        List<String> extraFeatures,

        @Schema(description = "Average rating", example = "4.5")
        Double ratingAvg,

        @Schema(description = "Total number of ratings", example = "25")
        Integer ratingCount,

        @Schema(description = "Latitude of the parking lot", example = "-34.6037")
        Double lat,

        @Schema(description = "Longitude of the parking lot", example = "-58.3816")
        Double lng,

        @Schema(description = "Type of access", example = "QR_CODE", allowableValues = {"QR_CODE", "PIN_CODE", "MANUAL_CONTACT", "OTHER"})
        AccessType accessType,

        @Schema(description = "Instructions for accessing the parking", example = "Call the intercom upon arrival")
        String accessInstructions,

        @Schema(description = "List of shifts associated with the parking")
        List<ShiftResponseDTO> shift
) {}