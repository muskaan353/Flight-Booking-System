package com.project.flight.Flight_Controller;

import com.project.flight.Flight_Service.FlightService;
import com.project.flight.dto.FlightDetailsDTO;
import com.project.flight.dto.SeatReservationResponse;
import com.project.flight.enm.FlightStatus;
import com.project.flight.model.Flight;
import com.project.flight.model.FlightSchedule;
import com.project.flight.model.Seat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightDetailsDTO>> searchFlights(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {
        return ResponseEntity.ok(flightService.searchFlightsWithDetails(source, destination, departureDate));
    }

    @PostMapping("/add")
    public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) {
        Flight savedFlight = flightService.addFlight(flight);
        return new ResponseEntity<>(savedFlight, HttpStatus.CREATED);
    }

    @PostMapping("/schedule/add")
    public ResponseEntity<FlightSchedule> addFlightSchedule(@RequestBody FlightSchedule schedule) {
        FlightSchedule savedSchedule = flightService.addFlightSchedule(schedule);
        return new ResponseEntity<>(savedSchedule, HttpStatus.CREATED);
    }

    @PostMapping("/seats/add")
    public ResponseEntity<Seat> addSeat(@RequestBody Seat seat) {
        Seat savedSeat = flightService.addSeat(seat);
        return new ResponseEntity<>(savedSeat, HttpStatus.CREATED);
    }

    @GetMapping("/schedule/all")
    public ResponseEntity<List<FlightSchedule>> getAllSchedules() {
        return ResponseEntity.ok(flightService.getAllSchedules());
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<FlightSchedule> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getScheduleById(id));
    }

    @PutMapping("/schedule/{id}")
    public ResponseEntity<FlightSchedule> updateFlightSchedule(
            @PathVariable Long id,
            @RequestBody FlightSchedule newDetails) {
        return ResponseEntity.ok(flightService.updateFlightSchedule(id, newDetails));
    }

    @PatchMapping("/schedule/{id}/status")
    public ResponseEntity<String> updateFlightStatus(
            @PathVariable Long id,
            @RequestParam FlightStatus status) {
        flightService.updateFlightStatus(id, status);
        return ResponseEntity.ok("Flight status updated successfully");
    }

 

    @GetMapping("/airline/{airline}")
    public ResponseEntity<List<Flight>> searchByAirline(@PathVariable String airline) {
        return ResponseEntity.ok(flightService.searchByAirline(airline));
    }

    @GetMapping("/seats/{flightNumber}")
    public ResponseEntity<List<Seat>> getSeatsByFlight(@PathVariable String flightNumber) {
        return ResponseEntity.ok(flightService.getSeatsByFlight(flightNumber));
    }

    // Reserve seats by seat class 
    @PutMapping("/reserveSeats")
    public ResponseEntity<SeatReservationResponse> reserveSeats(
            @RequestParam String flightNumber,
            @RequestParam String seatClass,
            @RequestParam int passengers) {
        boolean reserved = flightService.reserveSeats(flightNumber, seatClass, passengers);
        if (reserved) {
            return ResponseEntity.ok(new SeatReservationResponse(true, "Seats reserved successfully in " + seatClass + " class"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SeatReservationResponse(false, "Not enough seats available in " + seatClass + " class"));
        }
    }

    @GetMapping("/flight/{flightNumber}")
    public ResponseEntity<FlightDetailsDTO> getFlightDetailsByFlightNumber(@PathVariable String flightNumber) {
        return ResponseEntity.ok(flightService.getFlightDetailsByFlightNumber(flightNumber));
    }

}
