package com.igrowker.feature.parkify.features.parkingV2.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.TurnoRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.TipoRecurrencia;
import com.igrowker.feature.parkify.features.parkingV2.entities.Turno;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TurnoValidator {

    public List<String> validateBaseRules(TurnoRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            errors.add("horaInicio y horaFin son obligatorios.");
            return errors; // no se puede continuar sin estas horas
        }

        if (dto.getHoraInicio().equals(dto.getHoraFin())) {
            errors.add("horaInicio y horaFin no pueden ser iguales.");
        }

        if (dto.getPrecioPorHora() == null || dto.getPrecioPorHora().doubleValue() <= 0) {
            errors.add("precioPorHora debe ser un número mayor a cero.");
        }

        if (dto.getTipoRecurrencia() == null) {
            errors.add("tipoRecurrencia es obligatorio.");
        }

        if (dto.getTipoRecurrencia() == TipoRecurrencia.DIAS_ESPECIFICOS) {
            if (dto.getDiasEspecificos() == null || dto.getDiasEspecificos().isEmpty()) {
                errors.add("Si tipoRecurrencia es 'DIAS_ESPECIFICOS', debe incluir al menos un día.");
            } else if (!dto.getDiasEspecificos().stream().allMatch(d -> d >= 0 && d <= 6)) {
                errors.add("Los días específicos deben estar entre 0 (Dom) y 6 (Sáb).");
            }
        }

        return errors;
    }

    public List<String> validateNoOverlap(TurnoRequestDTO dto, List<Turno> turnosExistentes) {
        List<String> errors = new ArrayList<>();

        List<Intervalo> nuevos = descomponerEnIntervalos(dto.getHoraInicio(), dto.getHoraFin());

        for (Turno existente : turnosExistentes) {
            List<Intervalo> actuales = descomponerEnIntervalos(
                existente.getHoraInicio(), existente.getHoraFin());

            for (Intervalo nuevo : nuevos) {
                for (Intervalo actual : actuales) {
                    if (nuevo.seSuperponeCon(actual)) {
                        errors.add("El turno se superpone con otro existente: "
                            + existente.getHoraInicio() + " - " + existente.getHoraFin());
                        return errors;
                    }
                }
            }
        }

        return errors;
    }

    private List<Intervalo> descomponerEnIntervalos(LocalTime inicio, LocalTime fin) {
        int minInicio = inicio.toSecondOfDay() / 60;
        int minFin = fin.toSecondOfDay() / 60;

        if (minFin <= minInicio) {
            // Overnight
            return List.of(
                new Intervalo(minInicio, 1440),
                new Intervalo(0, minFin)
            );
        } else {
            return List.of(new Intervalo(minInicio, minFin));
        }
    }

    // Record de utilidad
    private record Intervalo(int desdeMin, int hastaMin) {
        boolean seSuperponeCon(Intervalo otro) {
            return this.desdeMin < otro.hastaMin && otro.desdeMin < this.hastaMin;
        }
    }
}
