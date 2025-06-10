package com.igrowker.feature.parkify.features.parkingV2.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.AccessType;
import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import com.igrowker.feature.parkify.features.parkingV2.repository.ParkingRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ParkingValidator {

    @Qualifier("parkingRepositoryV2")
    private final ParkingRepository parkingRepository;

    public List<String> validateForCreate(ParkingRequestDTO dto, Long ownerId) {
        return validateCommon(dto, null, ownerId, null);
    }

    public List<String> validateForUpdate(ParkingRequestDTO dto, Parking existingParking, Long ownerId) {
        return validateCommon(dto, existingParking.getId(), ownerId, existingParking);
    }

    private List<String> validateCommon(ParkingRequestDTO dto, UUID excludeId, Long ownerId, Parking existingParking) {
        List<String> errors = new ArrayList<>();

        if (dto.getAvailableSpots() > dto.getTotalSpots()) {
            errors.add("availableSpots no puede ser mayor a totalSpots");
        }

        if (dto.getLat() == null) {
            errors.add("La latitud es obligatoria");
        } else if (dto.getLat() < -90 || dto.getLat() > 90) {
            errors.add("Latitud fuera de rango (-90 a 90)");
        }

        if (dto.getLng() == null) {
            errors.add("La longitud es obligatoria");
        } else if (dto.getLng() < -180 || dto.getLng() > 180) {
            errors.add("Longitud fuera de rango (-180 a 180)");
        }

        if (dto.getParkingName() != null && dto.getParkingAddress() != null) {
            boolean existsSameNameAddressOwner = (excludeId == null)
                    ? parkingRepository.existsByParkingNameAndParkingAddressAndOwnerId(dto.getParkingName(), dto.getParkingAddress(), ownerId)
                    : parkingRepository.existsByParkingNameAndParkingAddressAndOwnerIdAndIdNot(dto.getParkingName(), dto.getParkingAddress(), ownerId, excludeId);

            if (existsSameNameAddressOwner) {
                errors.add("Ya tenés un parking con ese nombre y dirección");
            }
        }

        if (dto.getLat() != null && dto.getLng() != null) {
            boolean coordsChanged = existingParking == null
                    || !dto.getLat().equals(existingParking.getLat())
                    || !dto.getLng().equals(existingParking.getLng());

            if (coordsChanged) {
                boolean existsSameLocation = (excludeId == null)
                        ? parkingRepository.existsByLatAndLng(dto.getLat(), dto.getLng())
                        : parkingRepository.existsByLatAndLngAndIdNot(dto.getLat(), dto.getLng(), excludeId);

                if (existsSameLocation) {
                    errors.add("Ya existe un parking registrado en estas coordenadas");
                }
            }
        }

        
        boolean hasAccessInstructions = dto.getAccessInstructions() != null && !dto.getAccessInstructions().isBlank();
        boolean hasAccessType = dto.getAccessType() != null;

        if (hasAccessInstructions && !hasAccessType) {
            errors.add("Si se proporcionan instrucciones de acceso, también se debe especificar el tipo de acceso (accessType)");
        }

        if (hasAccessType && !hasAccessInstructions) {
            errors.add("Si se especifica un tipo de acceso, también se deben proporcionar las instrucciones de acceso (accessInstructions)");
        }

        return errors;
    }
}