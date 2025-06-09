package com.igrowker.feature.parkify.features.parkingV2.service;

import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ShiftRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ShiftResponseDTO;

public interface ShiftService {
    ShiftResponseDTO create(UUID parkingId, ShiftRequestDTO dto);
    List<ShiftResponseDTO> findByParkingId(UUID parkingId);
    ShiftResponseDTO update(UUID parkingId, UUID shiftId, ShiftRequestDTO dto);
    void delete(UUID parkingId, UUID shiftId);
}