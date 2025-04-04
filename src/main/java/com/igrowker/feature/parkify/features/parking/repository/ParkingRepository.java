package com.igrowker.feature.parkify.features.parking.repository;

import com.igrowker.feature.parkify.features.parking.entities.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
