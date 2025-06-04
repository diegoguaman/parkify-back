package com.igrowker.feature.parkify.features.parkingV2.dto.request;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.parkingV2.entities.TipoRecurrencia;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TurnoRequestDTO {

    @NotNull
    private UUID parkingId;

    private String nombre;

    @NotNull
    private LocalTime horaInicio;

    @NotNull
    private LocalTime horaFin;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precioPorHora;

    @NotNull
    private TipoRecurrencia tipoRecurrencia;
    
    private List<Integer> diasEspecificos;
}
