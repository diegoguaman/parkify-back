package com.igrowker.feature.parkify.features.parkingV2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.igrowker.feature.parkify.features.auth.security.AuthUserProvider;
import com.igrowker.feature.parkify.features.parkingV2.dto.request.ShiftRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ShiftResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import com.igrowker.feature.parkify.features.parkingV2.entities.Shift;
import com.igrowker.feature.parkify.features.parkingV2.repository.ParkingRepository;
import com.igrowker.feature.parkify.features.parkingV2.repository.ShiftRepository;
import com.igrowker.feature.parkify.features.parkingV2.validator.ShiftValidator;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ParkingRepository parkingRepository;
    private final ShiftValidator shiftValidator;

    public ShiftServiceImpl(ShiftRepository shiftRepository, @Qualifier("parkingRepositoryV2") ParkingRepository parkingRepository, ShiftValidator shiftValidator) {
        this.shiftRepository = shiftRepository;
        this.parkingRepository = parkingRepository;
        this.shiftValidator = shiftValidator;
    }

    @Override
    public ShiftResponseDTO create(UUID parkingId, ShiftRequestDTO dto) {
        Parking parking = findParkingAndVerifyOwnership(parkingId);

        List<String> errors = new ArrayList<>();
        errors.addAll(shiftValidator.validateBaseRules(dto));
        errors.addAll(shiftValidator.validateNoOverlap(dto, shiftRepository.findByParkingId(parkingId)));

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        Shift shift = new Shift();
        shift.setParking(parking);
        shift.setName(dto.getName());
        shift.setStartTime(dto.getStartTime());
        shift.setEndTime(dto.getEndTime());
        shift.setPricePerHour(dto.getPricePerHour());
        shift.setRecurrenceType(dto.getRecurrenceType());
        shift.setSpecificDays(dto.getSpecificDays());
        shift.setOvernight(dto.isOvernight());

        Shift saved = shiftRepository.save(shift);

        return new ShiftResponseDTO(
            saved.getId(),
            saved.getName(),
            saved.getStartTime(),
            saved.getEndTime(),
            saved.getPricePerHour(),
            saved.getRecurrenceType(),
            saved.getSpecificDays(),
            saved.isOvernight()
        );
    }

    @Override
    public List<ShiftResponseDTO> findByParkingId(UUID parkingId) {
        return shiftRepository.findByParkingId(parkingId).stream()
        .map(t -> new ShiftResponseDTO(
            t.getId(),
            t.getName(),
            t.getStartTime(),
            t.getEndTime(),
            t.getPricePerHour(),
            t.getRecurrenceType(),
            t.getSpecificDays(),
            t.isOvernight()
        ))
        .collect(Collectors.toList());
    }

    @Override
    public ShiftResponseDTO update(UUID parkingId, UUID shiftId, ShiftRequestDTO dto) {
        Shift shift = findShiftAndVerifyOwnership(shiftId, parkingId);

        // ✅ Validaciones
        List<String> errors = new ArrayList<>();
        errors.addAll(shiftValidator.validateBaseRules(dto));

        List<Shift> existentes = shiftRepository.findByParkingId(parkingId).stream()
                .filter(t -> !t.getId().equals(shiftId)) 
                .toList();

        errors.addAll(shiftValidator.validateNoOverlap(dto, existentes));

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }

        // ✅ Actualización
        shift.setName(dto.getName());
        shift.setStartTime(dto.getStartTime());
        shift.setEndTime(dto.getEndTime());
        shift.setPricePerHour(dto.getPricePerHour());
        shift.setRecurrenceType(dto.getRecurrenceType());
        shift.setSpecificDays(dto.getSpecificDays());
        shift.setOvernight(dto.isOvernight());

        Shift updated = shiftRepository.save(shift);

        return new ShiftResponseDTO(
            updated.getId(),
            updated.getName(),
            updated.getStartTime(),
            updated.getEndTime(),
            updated.getPricePerHour(),
            updated.getRecurrenceType(),
            updated.getSpecificDays(),
            updated.isOvernight()
        );
    }

    @Override
    public void delete(UUID parkingId, UUID shiftId) {
        Shift shift = findShiftAndVerifyOwnership(shiftId, parkingId);
        shiftRepository.delete(shift);
    }

    private Parking findParkingAndVerifyOwnership(UUID parkingId) {
        Long userId = AuthUserProvider.getAuthenticatedUserId();
        return parkingRepository.findById(parkingId)
            .map(p -> {
                if (!p.getOwner().getId().equals(userId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to operate on this parking.");
                }
                return p;
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking not found"));
    }

    private Shift findShiftAndVerifyOwnership(UUID shiftId, UUID parkingId) {
        Long userId = AuthUserProvider.getAuthenticatedUserId();
        return shiftRepository.findById(shiftId)
            .map(s -> {
                if (!s.getParking().getId().equals(parkingId)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The shift does not belong to the specified parking.");
                }
                if (!s.getParking().getOwner().getId().equals(userId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to operate on this shift.");
                }
                return s;
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shift not found"));
    }

}