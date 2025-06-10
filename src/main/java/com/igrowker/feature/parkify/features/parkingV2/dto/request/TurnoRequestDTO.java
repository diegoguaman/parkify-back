package com.igrowker.feature.parkify.features.parkingV2.dto.request;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.parkingV2.entities.TipoRecurrencia;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TurnoRequestDTO {

    @NotNull
    private UUID parkingId;

    private String nombre;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
      type        = "string",
      format      = "time",
      pattern     = "HH:mm:ss",
      description = "Hora de inicio en formato HH:mm:ss",
      example     = "08:00:00"
    )
    private LocalTime horaInicio;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(
      type        = "string",
      format      = "time",
      pattern     = "HH:mm:ss",
      description = "Hora de fin en formato HH:mm:ss",
      example     = "20:00:00"
    )
    private LocalTime horaFin;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precioPorHora;

    @NotNull
    private TipoRecurrencia tipoRecurrencia;
    
    private List<Integer> diasEspecificos;
}
