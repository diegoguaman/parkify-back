package com.igrowker.feature.parkify.features.parkingV2.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

import com.igrowker.feature.parkify.features.auth.entities.AuthUser;
import com.igrowker.feature.parkify.features.auth.repository.AuthUserRepository;
import com.igrowker.feature.parkify.features.auth.security.AuthUserProvider;
import com.igrowker.feature.parkify.features.auth.security.CustomUserDetails;
import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ParkingResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import com.igrowker.feature.parkify.features.parkingV2.mapper.ParkingMapper;
import com.igrowker.feature.parkify.features.parkingV2.repository.ParkingRepository;
import com.igrowker.feature.parkify.features.parkingV2.validator.ParkingValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("parkingV2ServiceImpl")
public class ParkingServiceImpl implements ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingMapper parkingMapper;
    private final ParkingValidator parkingValidator;
    private final AuthUserRepository authUserRepository;

    public ParkingServiceImpl(@Qualifier("parkingRepositoryV2") ParkingRepository parkingRepository,
            ParkingMapper parkingMapper, ParkingValidator parkingValidator, AuthUserRepository authUserRepository) {
        this.parkingRepository = parkingRepository;
        this.parkingMapper = parkingMapper;
        this.parkingValidator = parkingValidator;
        this.authUserRepository = authUserRepository;

    }

    @Override
    public ParkingResponseDTO create(ParkingRequestDTO request) {
        Long ownerId = AuthUserProvider.getAuthenticatedUserId();
        List<String> errors = parkingValidator.validateForCreate(request, ownerId);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
        Parking entity = parkingMapper.toEntity(request);
        AuthUser owner = authUserRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        entity.setOwner(owner);

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
        Long ownerId = AuthUserProvider.getAuthenticatedUserId();
        Parking existing = parkingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking not found"));
        
        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para modificar este parking");
        }
        List<String> errors = parkingValidator.validateForUpdate(request, existing, ownerId);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        parkingMapper.updateEntityFromDto(request, existing);
        return parkingMapper.toDto(parkingRepository.save(existing));
    }

    @Override
    public void delete(UUID id) {
        Parking parking = parkingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El parking no existe"));

        Long userId = AuthUserProvider.getAuthenticatedUserId(); 
        if (!parking.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar este parking");
        }

        parkingRepository.delete(parking);
    }

    
}
