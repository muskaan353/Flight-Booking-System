package com.project.flight.servicetest;

import com.project.flight.Flight_Repository.FlightRepository;
import com.project.flight.Flight_Repository.FlightScheduleRepository;
import com.project.flight.Flight_Repository.SeatRepository;
import com.project.flight.Flight_Service.FlightService;
import com.project.flight.dto.FlightDetailsDTO;
import com.project.flight.enm.FlightStatus;
import com.project.flight.enm.SeatClass;
import com.project.flight.exception.FlightNotFoundException;
import com.project.flight.model.Flight;
import com.project.flight.model.FlightSchedule;
import com.project.flight.model.Seat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightScheduleRepository flightScheduleRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;
    private FlightSchedule testSchedule;
    private Seat testSeat;

    @BeforeEach
    void setup() {
        testFlight = new Flight();
        testFlight.setFlightId(1L);
        testFlight.setFlightNumber("AI101");
        testFlight.setAirline("Air India");

        testSchedule = new FlightSchedule();
        testSchedule.setScheduleId(1L);
        testSchedule.setFlight(testFlight);
        testSchedule.setSource("DEL");
        testSchedule.setDestination("BLR");
        testSchedule.setDepartureDate(LocalDate.now());
        testSchedule.setDepartureTime(LocalTime.of(10, 30));
        testSchedule.setArrivalTime(LocalTime.of(13, 30));
        testSchedule.setDuration("3h");

        testSeat = new Seat();
        testSeat.setId(1L);
        testSeat.setFlight(testFlight);
        testSeat.setSeatClass(SeatClass.ECONOMY);
        testSeat.setAvailableSeats(20);
        testSeat.setDynamicFare(5000.0);
    }

    @Test
    void testSearchByFlightNumber_success() {
        when(flightRepository.findByFlightNumber("AI101")).thenReturn(Optional.of(testFlight));
        Flight result = flightService.searchByFlightNumber("AI101");
        assertEquals("Air India", result.getAirline());
    }

    @Test
    void testSearchByFlightNumber_notFound() {
        when(flightRepository.findByFlightNumber("AI404")).thenReturn(Optional.empty());
        assertThrows(FlightNotFoundException.class, () -> flightService.searchByFlightNumber("AI404"));
    }

    @Test
    void testGetAllFlights_returnsList() {
        when(flightRepository.findAll()).thenReturn(Collections.singletonList(testFlight));
        List<Flight> flights = flightService.getAllFlights();
        assertEquals(1, flights.size());
    }

//    @Test
//    void testReserveSeats_success() {
//        testSeat.setAvailableSeats(10);
//        when(seatRepository.findByFlight_FlightNumber("AI101")).thenReturn(Collections.singletonList(testSeat));
//
//        boolean result = flightService.reserveSeats("AI101", "ECONOMY", 5);
//        assertTrue(result);
//        verify(seatRepository, times(1)).save(any(Seat.class));
//    }

    @Test
    void testReserveSeats_insufficientSeats() {
        testSeat.setAvailableSeats(2);
        when(seatRepository.findByFlight_FlightNumber("AI101")).thenReturn(Collections.singletonList(testSeat));

        boolean result = flightService.reserveSeats("AI101", "ECONOMY", 5);
        assertFalse(result);
    }

    @Test
    void testSearchFlightsWithDetails_returnsDTOs() {
        when(flightScheduleRepository.findBySourceAndDestinationAndDepartureDate("DEL", "BLR", LocalDate.now()))
                .thenReturn(Collections.singletonList(testSchedule));

        when(seatRepository.findByFlight_FlightNumber("AI101"))
                .thenReturn(Collections.singletonList(testSeat));

        List<FlightDetailsDTO> dtos = flightService.searchFlightsWithDetails("DEL", "BLR", LocalDate.now());
        assertEquals(1, dtos.size());
        assertEquals("Air India", dtos.get(0).getAirline());
    }

    @Test
    void testUpdateFlightStatus_success() {
        when(flightScheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        flightService.updateFlightStatus(1L, FlightStatus.DEPARTED);
        assertEquals(FlightStatus.DEPARTED, testSchedule.getStatus());
        verify(flightScheduleRepository, times(1)).save(testSchedule);
    }
}
