package com.igrowker.feature.parkify.features.parkingV2.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ParkingResponseDTO {

    private UUID id;
    private UUID ownerId;
    private String parkingName;
    private String parkingAddress;
    private String parkingPhone;
    private String imageUrl;
    private int totalSpots;
    private int availableSpots;
    private List<String> extraFeatures;
    private Double ratingAvg;
    private Integer ratingCount;
    private Double lat;
    private Double lng;
    private List<TurnoResponseDTO> turnos;

}