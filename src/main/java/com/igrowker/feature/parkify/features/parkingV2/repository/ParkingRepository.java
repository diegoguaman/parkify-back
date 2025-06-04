package com.igrowker.feature.parkify.features.parkingV2.repository;

import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParkingRepository extends JpaRepository<Parking, UUID> {
    boolean existsByParkingNameAndLatAndLng(String parkingName, Double lat, Double lng);
    boolean existsByParkingNameAndLatAndLngAndIdNot(String parkingName, Double lat, Double lng, UUID id);

}