package com.igrowker.feature.parkify.features.parkingV2.mapper;

import org.springframework.stereotype.Component;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ParkingRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.ParkingResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.dto.response.TurnoResponseDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import com.igrowker.feature.parkify.features.parkingV2.entities.Turno;

@Component
public class ParkingMapper {

    public Parking toEntity(ParkingRequestDTO dto) {
        Parking p = new Parking();
        p.setOwnerId(dto.getOwnerId());
        p.setParkingName(dto.getParkingName());
        p.setParkingAddress(dto.getParkingAddress());
        p.setParkingPhone(dto.getParkingPhone());
        p.setImageUrl(dto.getImageUrl());
        p.setTotalSpots(dto.getTotalSpots());
        p.setAvailableSpots(dto.getAvailableSpots());
        p.setExtraFeatures(dto.getExtraFeatures());
        p.setLat(dto.getLat());
        p.setLng(dto.getLng());
        return p;
    }

    public ParkingResponseDTO toDto(Parking parking) {
        ParkingResponseDTO dto = new ParkingResponseDTO();
        dto.setId(parking.getId());
        dto.setOwnerId(parking.getOwnerId());
        dto.setParkingName(parking.getParkingName());
        dto.setParkingAddress(parking.getParkingAddress());
        dto.setParkingPhone(parking.getParkingPhone());
        dto.setImageUrl(parking.getImageUrl());
        dto.setTotalSpots(parking.getTotalSpots());
        dto.setAvailableSpots(parking.getAvailableSpots());
        dto.setExtraFeatures(parking.getExtraFeatures());
        dto.setRatingAvg(parking.getRatingAvg());
        dto.setRatingCount(parking.getRatingCount());
        dto.setLat(parking.getLat());
        dto.setLng(parking.getLng());

        if (parking.getTurnos() != null) {
            dto.setTurnos(parking.getTurnos().stream()
                    .map(this::mapTurnoToDto)
                    .toList());
        }

        return dto;
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
    }

    private TurnoResponseDTO mapTurnoToDto(Turno t) {
        TurnoResponseDTO dto = new TurnoResponseDTO();
        dto.setId(t.getId());
        dto.setNombre(t.getNombre());
        dto.setHoraInicio(t.getHoraInicio());
        dto.setHoraFin(t.getHoraFin());
        dto.setPrecioPorHora(t.getPrecioPorHora());
        dto.setTipoRecurrencia(t.getTipoRecurrencia());
        dto.setDiasEspecificos(t.getDiasEspecificos());
        return dto;
    }
}