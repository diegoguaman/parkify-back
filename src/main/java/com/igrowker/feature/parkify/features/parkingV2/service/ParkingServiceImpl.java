package com.igrowker.feature.parkify.features.parkingV2.service;

import org.springframework.stereotype.Service;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ParkingResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import com.igrowker.feature.parkify.features.parkingV2.mapper.ParkingMapper;
import com.igrowker.feature.parkify.features.parkingV2.repository.ParkingRepository;
import com.igrowker.feature.parkify.features.parkingV2.validator.ParkingValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParkingServiceImpl implements ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingMapper parkingMapper;
    private final ParkingValidator parkingValidator;

    public ParkingServiceImpl(ParkingRepository parkingRepository, ParkingMapper parkingMapper, ParkingValidator parkingValidator) {
        this.parkingRepository = parkingRepository;
        this.parkingMapper = parkingMapper;
        this.parkingValidator = parkingValidator;
    }

    @Override
    public ParkingResponseDTO create(ParkingRequestDTO request) {
        List<String> errors = parkingValidator.validateForCreate(request);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
        Parking entity = parkingMapper.toEntity(request);
        return parkingMapper.toDto(parkingRepository.save(entity));
    }

    @Override
    public ParkingResponseDTO getById(UUID id) {
        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking not found"));
        return parkingMapper.toDto(parking);
    }

    @Override
    public List<ParkingResponseDTO> getAll() {
        return parkingRepository.findAll().stream()
                .map(parkingMapper::toDto)
                .toList();
    }

   @Override
    public ParkingResponseDTO update(UUID id, ParkingRequestDTO request) {
        Parking existing = parkingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Parking not found"));

        List<String> errors = parkingValidator.validateForUpdate(request, existing.getId());
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        parkingMapper.updateEntityFromDto(request, existing);
        return parkingMapper.toDto(parkingRepository.save(existing));
    }

    @Override
    public void delete(UUID id) {
        parkingRepository.deleteById(id);
    }
}
