package com.project.flight.Flight_Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.flight.model.Seat;
import com.project.flight.enm.SeatClass;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlight_FlightNumber(String flightNumber);

    List<Seat> findByFlight_FlightNumberAndSeatClass(String flightNumber, SeatClass seatClass);
}
