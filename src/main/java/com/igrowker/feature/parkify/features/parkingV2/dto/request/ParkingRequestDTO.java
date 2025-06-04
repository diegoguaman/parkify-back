package com.igrowker.feature.parkify.features.parkingV2.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ParkingRequestDTO {

    @NotNull
    private UUID ownerId;

    @NotBlank
    private String parkingName;

    @NotBlank
    private String parkingAddress;

    @NotBlank
    private String parkingPhone;

    private String imageUrl;

    @NotNull
    private Integer totalSpots;

    @NotNull
    private Integer availableSpots;

    private List<String> extraFeatures;
    private Double ratingAvg;           
    private Integer ratingCount;        

    @NotNull
    private Double lat;
    
    @NotNull
    private Double lng;

    private String accessType;  
}
