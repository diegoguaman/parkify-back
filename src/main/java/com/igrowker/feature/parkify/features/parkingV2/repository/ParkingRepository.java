package com.igrowker.feature.parkify.features.parkingV2.repository;

import com.igrowker.feature.parkify.features.parkingV2.entities.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository("parkingRepositoryV2")
public interface ParkingRepository extends JpaRepository<Parking, UUID> {
    boolean existsByParkingNameAndParkingAddressAndOwnerId(String parkingName, String parkingAddress, Long ownerId);
    boolean existsByParkingNameAndParkingAddressAndOwnerIdAndIdNot(String parkingName, String parkingAddress, Long ownerId, UUID id);
    boolean existsByLatAndLng(Double lat, Double lng);
    boolean existsByLatAndLngAndIdNot(Double lat, Double lng, UUID id);
}