package com.project.flight.Flight_Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.project.flight.Flight_Repository.FlightRepository;
import com.project.flight.Flight_Repository.FlightScheduleRepository;
import com.project.flight.Flight_Repository.SeatRepository;
import com.project.flight.dto.FlightDetailsDTO;
import com.project.flight.enm.FlightStatus;
import com.project.flight.enm.SeatClass;
import com.project.flight.exception.FlightNotFoundException;
import com.project.flight.exception.InvalidFlightDataException;
import com.project.flight.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightScheduleRepository flightScheduleRepository;

    @Autowired
    private SeatRepository seatRepository;

    public List<Flight> getAllFlights() {
        logger.info("Fetching all flights");
        return flightRepository.findAll();
    }

    public Flight addFlight(Flight flight) {
        if (flight == null || flight.getFlightNumber().isEmpty()) {
            logger.error("Invalid flight data provided: {}", flight);
            throw new InvalidFlightDataException("Flight data is invalid.");
        }
        logger.info("Adding flight with flight number: {}", flight.getFlightNumber());
        return flightRepository.save(flight);
    }

    public FlightSchedule addFlightSchedule(FlightSchedule schedule) {
        logger.info("Adding flight schedule for flight number: {}", schedule.getFlight().getFlightNumber());
        return flightScheduleRepository.save(schedule);
    }

    public List<FlightSchedule> getAllSchedules() {
        logger.info("Fetching all flight schedules");
        return flightScheduleRepository.findAll();
    }

    public FlightSchedule getScheduleById(Long id) {
        logger.info("Fetching schedule with ID: {}", id);
        return flightScheduleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Schedule with ID {} not found", id);
                    return new FlightNotFoundException("Schedule with ID " + id + " not found");
                });
    }

    public FlightSchedule updateFlightSchedule(Long id, FlightSchedule newDetails) {
        logger.info("Updating flight schedule with ID: {}", id);
        FlightSchedule schedule = getScheduleById(id);
        schedule.setDepartureTime(newDetails.getDepartureTime());
        schedule.setArrivalTime(newDetails.getArrivalTime());
        schedule.setStatus(newDetails.getStatus());
        schedule.setDuration(newDetails.getDuration());
        return flightScheduleRepository.save(schedule);
    }

    public void updateFlightStatus(Long scheduleId, FlightStatus status) {
        logger.info("Updating status of flight schedule with ID: {} to {}", scheduleId, status);
        FlightSchedule schedule = getScheduleById(scheduleId);
        schedule.setStatus(status);
        flightScheduleRepository.save(schedule);
    }

    public Flight searchByFlightNumber(String flightNumber) {
        logger.info("Searching for flight with flight number: {}", flightNumber);
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> {
                    logger.error("Flight with number {} not found", flightNumber);
                    return new FlightNotFoundException("Flight with number " + flightNumber + " not found");
                });
    }

    public List<Flight> searchByAirline(String airline) {
        logger.info("Searching for flights with airline: {}", airline);
        return flightRepository.findByAirline(airline);
    }

    public List<Seat> getSeatsByFlight(String flightNumber) {
        logger.info("Fetching seats for flight with flight number: {}", flightNumber);
        return seatRepository.findByFlight_FlightNumber(flightNumber);
    }

    public Seat addSeat(Seat seat) {
        Long flightId = seat.getFlight().getFlightId();
        logger.info("Adding seat for flight ID: {}", flightId);
        Flight flight = flightRepository.findById(flightId)
            .orElseThrow(() -> {
                logger.error("Flight with ID {} not found", flightId);
                return new FlightNotFoundException("Flight not found with ID: " + flightId);
            });

        seat.setFlight(flight);
        return seatRepository.save(seat);
    }

    public List<FlightSchedule> searchFlights(String source, String destination, LocalDate departureDate) {
        logger.info("Searching flights from {} to {} on {}", source, destination, departureDate);
        return flightScheduleRepository.findBySourceAndDestinationAndDepartureDate(source, destination, departureDate);
    }

    public List<FlightDetailsDTO> searchFlightsWithDetails(String source, String destination, LocalDate departureDate) {
        logger.info("Searching flights with details from {} to {} on {}", source, destination, departureDate);
        List<FlightSchedule> schedules = searchFlights(source, destination, departureDate);
        return getFlightDetails(schedules);
    }

    public List<FlightDetailsDTO> getFlightDetails(List<FlightSchedule> schedules) {
        logger.info("Converting flight schedules to flight details DTOs");
        return schedules.stream().map(this::extractFlightDetails).collect(Collectors.toList());
    }

    private FlightDetailsDTO extractFlightDetails(FlightSchedule schedule) {
        FlightDetailsDTO dto = new FlightDetailsDTO();
        Flight flight = schedule.getFlight();

        if (flight != null) {
            dto.setFlightNumber(flight.getFlightNumber());
            dto.setAirline(flight.getAirline());
        }

        dto.setSource(schedule.getSource());
        dto.setDestination(schedule.getDestination());
        dto.setDepartureDate(schedule.getDepartureDate());
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setDuration(schedule.getDuration());

        List<Seat> seats = (flight != null)
                ? seatRepository.findByFlight_FlightNumber(flight.getFlightNumber())
                : Collections.emptyList();

        Map<String, Integer> availableSeatsPerClass = seats.stream().collect(Collectors.toMap(
                seat -> seat.getSeatClass().name(),
                Seat::getAvailableSeats
        ));

        Map<String, Double> pricePerClass = seats.stream().collect(Collectors.toMap(
                seat -> seat.getSeatClass().name(),
                Seat::getDynamicFare
        ));

        dto.setAvailableSeatsPerClass(availableSeatsPerClass);
        dto.setPricePerClass(pricePerClass);

        return dto;
    }

    public boolean reserveSeats(String flightNumber, String seatClass, int passengers) {
        logger.info("Attempting to reserve {} passengers in seat class {} for flight {}", passengers, seatClass, flightNumber);
        List<Seat> seats = seatRepository.findByFlight_FlightNumber(flightNumber);

        if (seats.isEmpty()) {
            logger.error("No seats found for flight with number {}", flightNumber);
            throw new FlightNotFoundException("No seats found for flight with number " + flightNumber);
        }

        SeatClass requestedClass;
        try {
            requestedClass = SeatClass.valueOf(seatClass.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid seat class provided: {}", seatClass);
            throw new InvalidFlightDataException("Invalid seat class: " + seatClass);
        }

        Optional<Seat> seatOptional = seats.stream()
                .filter(seat -> seat.getSeatClass() == requestedClass)
                .findFirst();

        if (!seatOptional.isPresent()) {
            logger.error("Seat class {} not available for this flight {}", seatClass, flightNumber);
            throw new FlightNotFoundException("Seat class " + seatClass + " not available for this flight");
        }

        Seat seat = seatOptional.get();
        if (seat.getAvailableSeats() < passengers) {
            logger.warn("Not enough available seats for class {} in flight {}", seatClass, flightNumber);
            return false;
        }

        boolean booked = seat.bookSeats(passengers);
        if (booked) {
            seatRepository.save(seat);
            logger.info("Successfully booked {} passengers in seat class {} for flight {}", passengers, seatClass, flightNumber);
        }

        return booked;
    }

    public FlightDetailsDTO getFlightDetailsByFlightNumber(String flightNumber) {
        logger.info("Fetching flight details for flight number: {}", flightNumber);
        Flight flight = searchByFlightNumber(flightNumber);
        List<FlightSchedule> schedules = flightScheduleRepository.findByFlight_FlightNumber(flightNumber);

        if (schedules.isEmpty()) {
            logger.error("No schedule found for flight number {}", flightNumber);
            throw new FlightNotFoundException("No schedule found for flight " + flightNumber);
        }

        return extractFlightDetails(schedules.get(0));
    }
}
