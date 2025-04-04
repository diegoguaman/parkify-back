package com.igrowker.feature.parkify.features.booking.repository;

import com.igrowker.feature.parkify.features.booking.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
