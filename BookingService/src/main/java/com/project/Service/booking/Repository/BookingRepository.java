package com.project.Service.booking.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.Service.booking.enm.BookingStatus;
import com.project.Service.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByFlightNumber(String flightNumber);
    List<Booking> findByBookingStatus(BookingStatus status);

}
