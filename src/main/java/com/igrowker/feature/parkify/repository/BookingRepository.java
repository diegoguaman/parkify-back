package com.igrowker.feature.parkify.repository;

import com.igrowker.feature.parkify.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
