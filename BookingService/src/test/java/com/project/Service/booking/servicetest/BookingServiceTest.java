package com.project.Service.booking.servicetest;
import com.project.Service.booking.Repository.BookingRepository;
import com.project.Service.booking.Service.BookingService;
import com.project.Service.booking.dto.*;
import com.project.Service.booking.enm.BookingStatus;
import com.project.Service.booking.exception.BookingNotFoundException;
import com.project.Service.booking.feign.FlightClient;
import com.project.Service.booking.feign.NotificationClient;
import com.project.Service.booking.feign.UserClient;
import com.project.Service.booking.model.Booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightClient flightClient;

    @Mock
    private UserClient userClient;

    @Mock
    private NotificationClient notificationClient;

    private Booking booking;
    private FlightDTO flight;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        booking = new Booking();
        booking.setId(1L);
        booking.setUserId(100L);
        booking.setFlightNumber("AI101");
        booking.setSeatClass("Economy");
        booking.setPassengers(2);

        flight = new FlightDTO();
        flight.setFlightNumber("AI101");
        flight.setAvailableSeatsPerClass(Map.of("Economy", 5));
        flight.setPricePerClass(Map.of("Economy", 200.0));
    }

    @Test
    void testCreateBooking_Success() {
        when(flightClient.getFlightByNumber("AI101")).thenReturn(flight);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.createBooking(booking);

        assertEquals(1L, result.getId());
        assertEquals(BookingStatus.PENDING, result.getBookingStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_FlightNotFound() {
        when(flightClient.getFlightByNumber("AI101")).thenReturn(null);

        BookingNotFoundException ex = assertThrows(BookingNotFoundException.class,
                () -> bookingService.createBooking(booking));

        assertEquals("Flight with number AI101 not found", ex.getMessage());
    }

    @Test
    void testGetAllBookings() {
        List<Booking> bookings = List.of(booking);
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
        verify(bookingRepository).findAll();
    }

    @Test
    void testGetBookingById_Found() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateBookingStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking updated = bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED);

        assertEquals(BookingStatus.CONFIRMED, updated.getBookingStatus());
    }

    @Test
    void testDeleteBooking_Cancelled() {
        booking.setBookingStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);
        verify(bookingRepository).deleteById(1L);
    }

    @Test
    void testDeleteBooking_NotCancelled() {
        booking.setBookingStatus(BookingStatus.PENDING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBooking(1L));
    }

    @Test
    void testConfirmBooking_Success() {
        ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setBookingId(1L);

        SeatReservationResponse seatResponse = new SeatReservationResponse();
        seatResponse.setSuccess(true);

        UserResponseDTO user = new UserResponseDTO();
        user.setEmail("test@example.com");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightClient.getFlightByNumber("AI101")).thenReturn(flight);
        when(flightClient.reserveSeats("AI101", 2, "Economy")).thenReturn(seatResponse);
        when(userClient.getUserById(100L)).thenReturn(user);

        String result = bookingService.confirmBooking(request);

        assertEquals("Booking Confirmed & Email Sent", result);
        verify(notificationClient).sendEmail(any(EmailRequestDTO.class));
    }

    @Test
    void testConfirmBooking_SeatReservationFailed() {
        ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setBookingId(1L);

        SeatReservationResponse seatResponse = new SeatReservationResponse();
        seatResponse.setSuccess(false);
        seatResponse.setMessage("Seats full");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightClient.getFlightByNumber("AI101")).thenReturn(flight);
        when(flightClient.reserveSeats("AI101", 2, "Economy")).thenReturn(seatResponse);

        assertThrows(BookingNotFoundException.class, () -> bookingService.confirmBooking(request));
    }
}
