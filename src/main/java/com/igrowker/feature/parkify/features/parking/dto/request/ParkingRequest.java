package com.igrowker.feature.parkify.features.parking.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParkingRequest {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double rateHour;
    private int available;
    private String whatsapp;
    @Schema(description = "Owner ID of the parking", required = true)
    private Long ownerId;
    private Long parkingId;
    @Schema(description = "Number of available spots for parking", required = true)
    private int availableSpots;
}
