package com.project.flight.Flight_Repository;


import com.project.flight.model.Flight;
import com.project.flight.model.FlightSchedule;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {

    List<FlightSchedule> findBySourceAndDestinationAndDepartureDate(
        String source, String destination, LocalDate departureDate
    );

    List<FlightSchedule> findByFlight(Flight flight);

    List<FlightSchedule> findByFlightDate(LocalDate flightDate);

	List<FlightSchedule> findByFlight_FlightNumber(String flightNumber);
}
