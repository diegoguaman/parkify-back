package com.igrowker.feature.parkify.features.parkingV2.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.repository.ParkingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ParkingValidator {

    private final ParkingRepository parkingRepository;

    public List<String> validateForCreate(ParkingRequestDTO dto) {
        return validateCommon(dto, null);
    }

    public List<String> validateForUpdate(ParkingRequestDTO dto, UUID existingParkingId) {
        return validateCommon(dto, existingParkingId);
    }

    private List<String> validateCommon(ParkingRequestDTO dto, UUID excludeId) {
        List<String> errors = new ArrayList<>();

        if (dto.getAvailableSpots() > dto.getTotalSpots()) {
            errors.add("availableSpots no puede ser mayor a totalSpots");
        }

        if (dto.getLat() == null || dto.getLat() < -90 || dto.getLat() > 90) {
            errors.add("Latitud fuera de rango (-90 a 90)");
        }

        if (dto.getLng() == null || dto.getLng() < -180 || dto.getLng() > 180) {
            errors.add("Longitud fuera de rango (-180 a 180)");
        }

        boolean exists = (excludeId == null)
                ? parkingRepository.existsByParkingNameAndLatAndLng(dto.getParkingName(), dto.getLat(), dto.getLng())
                : parkingRepository.existsByParkingNameAndLatAndLngAndIdNot(dto.getParkingName(), dto.getLat(), dto.getLng(), excludeId);

        if (exists) {
            errors.add("Ya existe un parking con este nombre y ubicación");
        }

        return errors;
    }
}