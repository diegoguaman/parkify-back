package com.igrowker.feature.parkify.features.parking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="parkings")
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double rateHour;
    private String whatsapp;

    @Column(name = "owner_id")
    private Long ownerId;
    @Column(name = "available_spots")
    private Integer availableSpots;
    @Column(nullable = false)
    private Integer capacity;
    private String workingHours;

}
