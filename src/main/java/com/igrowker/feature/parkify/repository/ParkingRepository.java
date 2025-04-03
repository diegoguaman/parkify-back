package com.igrowker.feature.parkify.repository;

import com.igrowker.feature.parkify.entities.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
