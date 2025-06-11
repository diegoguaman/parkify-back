package com.igrowker.feature.parkify.features.parkingV2.dto.request;

import java.util.List;

import com.igrowker.feature.parkify.features.parkingV2.entities.AccessType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ParkingRequestDTO {

    @Schema(description = "Name of the parking lot", example = "Parking Palermo", required = true)
    @NotBlank(message = "Parking name is required")
    private String parkingName;

    @Schema(description = "Address of the parking lot", example = "Av. Libertador 1234", required = true)
    @NotBlank(message = "Parking address is required")
    private String parkingAddress;

    @Schema(description = "Contact phone number of the parking lot", example = "1134567890", required = true)
    @NotBlank(message = "Parking phone number is required")
    private String parkingPhone;

    @Schema(description = "URL of a representative image", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Total number of spots in the parking lot", example = "20", required = true)
    @NotNull(message = "Total number of spots is required")
    private Integer totalSpots;

    @Schema(description = "Number of currently available spots", example = "15", required = true)
    @NotNull(message = "Number of available spots is required")
    private Integer availableSpots;

    @Schema(description = "List of additional features", example = "[\"covered\", \"24h surveillance\"]")
    private List<String> extraFeatures;

    @Schema(description = "Average rating", example = "4.5")
    private Double ratingAvg;

    @Schema(description = "Number of ratings received", example = "25")
    private Integer ratingCount;

    @Schema(description = "Latitude of the location", example = "-34.6037", required = true)
    @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
    @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
    @NotNull(message = "Latitude is required")
    private Double lat;

    @Schema(description = "Longitude of the location", example = "-58.3816", required = true)
    @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
    @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
    @NotNull(message = "Longitude is required")
    private Double lng;

    @Schema(
        description = "Access type for the parking lot",
        example = "QR_CODE",
        allowableValues = {"QR_CODE", "PIN_CODE", "MANUAL_CONTACT", "OTHER"}
    )
    private AccessType accessType;

    @Schema(description = "Additional access instructions", example = "Call the intercom upon arrival")
    private String accessInstructions;
}
