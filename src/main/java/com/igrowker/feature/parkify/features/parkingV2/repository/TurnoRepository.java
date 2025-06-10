package com.igrowker.feature.parkify.features.parkingV2.repository;

import com.igrowker.feature.parkify.features.parkingV2.entities.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TurnoRepository extends JpaRepository<Turno, UUID> {
    List<Turno> findByParkingId(UUID parkingId);
}