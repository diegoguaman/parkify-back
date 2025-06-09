package com.igrowker.feature.parkify.features.parkingV2.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
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
            errors.add("availableSpots cannot be greater than totalSpots");
        }

        if (dto.getParkingName() != null && dto.getParkingAddress() != null) {
            boolean existsSameNameAddressOwner = (excludeId == null)
                    ? parkingRepository.existsByParkingNameAndParkingAddressAndOwnerId(dto.getParkingName(), dto.getParkingAddress(), ownerId)
                    : parkingRepository.existsByParkingNameAndParkingAddressAndOwnerIdAndIdNot(dto.getParkingName(), dto.getParkingAddress(), ownerId, excludeId);

            if (existsSameNameAddressOwner) {
                errors.add("You already have a parking with that name and address");
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
                    errors.add("A parking already exists at these coordinates");
                }
            }
        }

        
        boolean hasAccessInstructions = dto.getAccessInstructions() != null && !dto.getAccessInstructions().isBlank();
        boolean hasAccessType = dto.getAccessType() != null;

        if (hasAccessInstructions && !hasAccessType) {
            errors.add("If accessInstructions are provided, accessType must also be specified");
        }

        if (hasAccessType && !hasAccessInstructions) {
            errors.add("If accessType is specified, accessInstructions must also be provided");
        }

        return errors;
    }
}