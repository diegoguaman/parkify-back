package com.igrowker.feature.parkify.features.parkingV2.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.parkingV2.entities.RecurrenceType;

@Schema(description = "DTO for returning shift data")
public record ShiftResponseDTO(
        @Schema(description = "ID of the shift", example = "8f1f0a33-3f9f-4f52-bd51-1243a5e874b1")
        UUID id,

        @Schema(description = "Name of the shift", example = "Night Shift")
        String name,

        @Schema(description = "Start time of the shift", example = "22:00")
        LocalTime startTime,

        @Schema(description = "End time of the shift", example = "02:00")
        LocalTime endTime,

        @Schema(description = "Hourly rate during this shift", example = "120.50")
        BigDecimal pricePerHour,

        @Schema(
                description = "Type of recurrence for the shift",
                example = "DIAS_ESPECIFICOS",
                allowableValues = {"DIARIO", "SEMANAL", "MENSUAL", "DIAS_ESPECIFICOS"}
        )
        RecurrenceType recurrenceType,

        @Schema(description = "Specific days when the shift applies (0 = Sunday, 6 = Saturday)", example = "[0, 1, 5]")
        List<Integer> specificDays,

        @Schema(description = "Indicates if the shift is overnight (crosses midnight)", example = "true")
        boolean overnight
) {}
