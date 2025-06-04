package com.igrowker.feature.parkify.features.parkingV2.service;


import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ParkingResponseDTO;

public interface ParkingService {
    ParkingResponseDTO create(ParkingRequestDTO request);
    ParkingResponseDTO getById(UUID id);
    List<ParkingResponseDTO> getAll();
    ParkingResponseDTO update(UUID id, ParkingRequestDTO request);
    void delete(UUID id);
}