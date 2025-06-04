package com.igrowker.feature.parkify.features.parkingV2.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.parkingV2.entities.TipoRecurrencia;
import lombok.Data;


@Data
public class TurnoResponseDTO {
    private UUID id;
    private String nombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal precioPorHora;
    private TipoRecurrencia tipoRecurrencia;
    private List<Integer> diasEspecificos;
}