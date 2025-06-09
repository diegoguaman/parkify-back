package com.igrowker.feature.parkify.features.parkingV2.dto.request;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import com.igrowker.feature.parkify.features.parkingV2.entities.RecurrenceType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShiftRequestDTO {

    @Schema(description = "Name or label of the shift", example = "Morning Shift")
    private String name;

    @Schema(description = "Start time of the shift", example = "08:00", required = true)
    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @Schema(description = "End time of the shift", example = "12:00", required = true)
    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Schema(description = "Hourly rate for the shift", example = "150.00", required = true, minimum = "0.01")
    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hourly rate must be greater than 0")
    private BigDecimal pricePerHour;

    @Schema(
        description = "Type of recurrence for the shift",
        example = "DAILY",
        required = true,
        allowableValues = {"DAILY", "WEEKLY", "MONTHLY", "SPECIFIC"}
    )
    @NotNull(message = "Recurrence type is required")
    private RecurrenceType recurrenceType;

    @Schema(
        description = "List of specific days (0 = Sunday, ..., 6 = Saturday). Required only if recurrence is SPECIFIC",
        example = "[1, 3, 5]"
    )
    private List<@Min(0) @Max(6) Integer> specificDays;
    @Schema(description = "Indicates whether the shift crosses midnight", example = "true")
    private boolean overnight;
}
