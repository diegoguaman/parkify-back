package com.igrowker.feature.parkify.features.parkingV2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.TurnoRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.TurnoResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import com.igrowker.feature.parkify.features.parkingV2.entities.Turno;
import com.igrowker.feature.parkify.features.parkingV2.repository.ParkingRepository;
import com.igrowker.feature.parkify.features.parkingV2.repository.TurnoRepository;
import com.igrowker.feature.parkify.features.parkingV2.validator.TurnoValidator;

@Service
public class TurnoServiceImpl implements TurnoService {

    private final TurnoRepository turnoRepository;
    private final ParkingRepository parkingRepository;
    private final TurnoValidator turnoValidator;

    public TurnoServiceImpl(TurnoRepository turnoRepository, @Qualifier("parkingRepositoryV2")ParkingRepository parkingRepository, TurnoValidator turnoValidator) {
        this.turnoRepository = turnoRepository;
        this.parkingRepository = parkingRepository;
        this.turnoValidator = turnoValidator;
    }

    @Override
    public TurnoResponseDTO create(TurnoRequestDTO dto) {
        Parking parking = parkingRepository.findById(dto.getParkingId())
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        List<String> errors = new ArrayList<>();
        errors.addAll(turnoValidator.validateBaseRules(dto));
        errors.addAll(turnoValidator.validateNoOverlap(dto, turnoRepository.findByParkingId(dto.getParkingId())));

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        Turno turno = new Turno();
        turno.setParking(parking);
        turno.setNombre(dto.getNombre());
        turno.setHoraInicio(dto.getHoraInicio());
        turno.setHoraFin(dto.getHoraFin());
        turno.setPrecioPorHora(dto.getPrecioPorHora());
        turno.setTipoRecurrencia(dto.getTipoRecurrencia());
        turno.setDiasEspecificos(dto.getDiasEspecificos());

        Turno saved = turnoRepository.save(turno);

        TurnoResponseDTO response = new TurnoResponseDTO();
        response.setId(saved.getId());
        response.setNombre(saved.getNombre());
        response.setHoraInicio(saved.getHoraInicio());
        response.setHoraFin(saved.getHoraFin());
        response.setPrecioPorHora(saved.getPrecioPorHora());
        response.setTipoRecurrencia(saved.getTipoRecurrencia());
        response.setDiasEspecificos(saved.getDiasEspecificos());
        return response;
    }

    @Override
    public List<TurnoResponseDTO> findByParkingId(UUID parkingId) {
        return turnoRepository.findByParkingId(parkingId).stream().map(t -> {
            TurnoResponseDTO dto = new TurnoResponseDTO();
            dto.setId(t.getId());
            dto.setNombre(t.getNombre());
            dto.setHoraInicio(t.getHoraInicio());
            dto.setHoraFin(t.getHoraFin());
            dto.setPrecioPorHora(t.getPrecioPorHora());
            dto.setTipoRecurrencia(t.getTipoRecurrencia());
            dto.setDiasEspecificos(t.getDiasEspecificos());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public TurnoResponseDTO update(UUID turnoId, TurnoRequestDTO dto) {
        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        Parking parking = parkingRepository.findById(dto.getParkingId())
                .orElseThrow(() -> new RuntimeException("Parking no encontrado"));

        // ✅ Validaciones
        List<String> errors = new ArrayList<>();
        errors.addAll(turnoValidator.validateBaseRules(dto));

        List<Turno> existentes = turnoRepository.findByParkingId(dto.getParkingId()).stream()
                .filter(t -> !t.getId().equals(turnoId)) // excluir turno actual
                .toList();

        errors.addAll(turnoValidator.validateNoOverlap(dto, existentes));

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        // ✅ Actualización
        turno.setParking(parking); // opcional si no permitís cambiar parkingId
        turno.setNombre(dto.getNombre());
        turno.setHoraInicio(dto.getHoraInicio());
        turno.setHoraFin(dto.getHoraFin());
        turno.setPrecioPorHora(dto.getPrecioPorHora());
        turno.setTipoRecurrencia(dto.getTipoRecurrencia());
        turno.setDiasEspecificos(dto.getDiasEspecificos());

        Turno updated = turnoRepository.save(turno);

        TurnoResponseDTO response = new TurnoResponseDTO();
        response.setId(updated.getId());
        response.setNombre(updated.getNombre());
        response.setHoraInicio(updated.getHoraInicio());
        response.setHoraFin(updated.getHoraFin());
        response.setPrecioPorHora(updated.getPrecioPorHora());
        response.setTipoRecurrencia(updated.getTipoRecurrencia());
        response.setDiasEspecificos(updated.getDiasEspecificos());

        return response;
    }

    @Override
    public void delete(UUID turnoId) {
        turnoRepository.deleteById(turnoId);
    }
}