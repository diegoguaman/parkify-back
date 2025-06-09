package com.igrowker.feature.parkify.features.parkingV2.mapper;

import org.springframework.stereotype.Component;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ParkingResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ShiftResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import com.igrowker.feature.parkify.features.parkingV2.entities.Shift;

@Component
public class ParkingMapper {

    public Parking toEntity(ParkingRequestDTO dto) {
        return Parking.builder()
                .parkingName(dto.getParkingName())
                .parkingAddress(dto.getParkingAddress())
                .parkingPhone(dto.getParkingPhone())
                .imageUrl(dto.getImageUrl())
                .totalSpots(dto.getTotalSpots())
                .availableSpots(dto.getAvailableSpots())
                .extraFeatures(dto.getExtraFeatures())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .accessType(dto.getAccessType())
                .accessInstructions(dto.getAccessInstructions())
                .build();
    }

    public ParkingResponseDTO toDto(Parking parking) {
        return new ParkingResponseDTO(
                parking.getId(),
                parking.getOwner() != null ? parking.getOwner().getId() : null,
                parking.getParkingName(),
                parking.getParkingAddress(),
                parking.getParkingPhone(),
                parking.getImageUrl(),
                parking.getTotalSpots(),
                parking.getAvailableSpots(),
                parking.getExtraFeatures(),
                parking.getRatingAvg(),
                parking.getRatingCount(),
                parking.getLat(),
                parking.getLng(),
                parking.getAccessType(),
                parking.getAccessInstructions(),
                parking.getShifts() != null
                        ? parking.getShifts().stream().map(this::mapShiftToDto).toList()
                        : null
        );
    }

    public void updateEntityFromDto(ParkingRequestDTO dto, Parking p) {
        p.setParkingName(dto.getParkingName());
        p.setParkingAddress(dto.getParkingAddress());
        p.setParkingPhone(dto.getParkingPhone());
        p.setImageUrl(dto.getImageUrl());
        p.setTotalSpots(dto.getTotalSpots());
        p.setAvailableSpots(dto.getAvailableSpots());
        p.setExtraFeatures(dto.getExtraFeatures());
        p.setLat(dto.getLat());
        p.setLng(dto.getLng());
        p.setAccessType(dto.getAccessType());
        p.setAccessInstructions(dto.getAccessInstructions());
    }

    private ShiftResponseDTO mapShiftToDto(Shift t) {
         return new ShiftResponseDTO(
                t.getId(),
                t.getName(),
                t.getStartTime(),
                t.getEndTime(),
                t.getPricePerHour(),
                t.getRecurrenceType(),
                t.getSpecificDays(),
                t.isOvernight()
        );
    }
}