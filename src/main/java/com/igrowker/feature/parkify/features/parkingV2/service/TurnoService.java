package com.igrowker.feature.parkify.features.parkingV2.service;

import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.TurnoRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.TurnoResponseDTO;

public interface TurnoService {
    TurnoResponseDTO create(TurnoRequestDTO dto);
    List<TurnoResponseDTO> findByParkingId(UUID parkingId);
    TurnoResponseDTO update(UUID turnoId, TurnoRequestDTO dto);
    void delete(UUID turnoId);
}