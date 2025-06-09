package com.igrowker.feature.parkify.features.parkingV2.repository;

import com.igrowker.feature.parkify.features.parkingV2.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findByParkingId(UUID parkingId);
}