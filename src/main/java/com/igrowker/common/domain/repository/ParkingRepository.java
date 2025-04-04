package com.igrowker.common.domain.repository;

import com.igrowker.common.domain.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
