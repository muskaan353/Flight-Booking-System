package com.project.Service.booking.Service;

import com.project.Service.booking.Repository.BookingRepository;
import com.project.Service.booking.dto.ConfirmBookingRequest;
import com.project.Service.booking.dto.EmailRequestDTO;
import com.project.Service.booking.dto.FlightDTO;
import com.project.Service.booking.dto.SeatReservationResponse;
import com.project.Service.booking.dto.UserResponseDTO;
import com.project.Service.booking.enm.BookingStatus;
import com.project.Service.booking.exception.BookingNotFoundException;
import com.project.Service.booking.feign.FlightClient;
import com.project.Service.booking.feign.NotificationClient;
import com.project.Service.booking.feign.UserClient;
import com.project.Service.booking.model.Booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightClient flightClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private NotificationClient notificationClient;

    public synchronized Booking createBooking(Booking booking) {
        logger.info("Creating booking for User ID: {}", booking.getUserId());

        FlightDTO flight = flightClient.getFlightByNumber(booking.getFlightNumber());
        if (flight == null) {
            throw new BookingNotFoundException("Flight with number " + booking.getFlightNumber() + " not found");
        }

        String seatClass = booking.getSeatClass();
        int requestedSeats = booking.getPassengers();

        Map<String, Integer> availableSeatsMap = flight.getAvailableSeatsPerClass();
        Integer availableSeats = availableSeatsMap.getOrDefault(seatClass, 0);

        if (availableSeats < requestedSeats) {
            throw new BookingNotFoundException("Not enough " + seatClass + " seats available for flight " + booking.getFlightNumber());
        }

        Map<String, Double> priceMap = flight.getPricePerClass();
        Double pricePerSeat = priceMap.getOrDefault(seatClass, 0.0);
        booking.setTotalPrice(pricePerSeat * requestedSeats);

        booking.setBookingStatus(BookingStatus.PENDING);
        Booking savedBooking = bookingRepository.save(booking);

        logger.info("Booking created successfully with ID: {}", savedBooking.getId());
        return savedBooking;
    }

    public List<Booking> getAllBookings() {
        logger.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        logger.info("Fetching booking with ID: {}", id);
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + id + " not found"));
    }

    public Map<String, Object> getBookingDetails(Long bookingId) {
        logger.info("Fetching booking details for ID: {}", bookingId);
        Booking booking = getBookingById(bookingId);

        if (booking.getFlightNumber() == null || booking.getFlightNumber().isEmpty()) {
            throw new BookingNotFoundException("Flight number is missing for booking ID " + bookingId);
        }

        FlightDTO flight = flightClient.getFlightByNumber(booking.getFlightNumber());
        if (flight == null) {
            throw new BookingNotFoundException("Flight details not available for flight number " + booking.getFlightNumber());
        }

        Map<String, Object> details = new HashMap<>();
        details.put("bookingId", booking.getId());
        details.put("userId", booking.getUserId());
        details.put("flightNumber", booking.getFlightNumber());
        details.put("flightDetails", flight);
        details.put("bookingDate", booking.getBookingDate());
        details.put("passengers", booking.getPassengers());
        details.put("seatClass", booking.getSeatClass());
        details.put("totalPrice", booking.getTotalPrice());
        details.put("bookingStatus", booking.getBookingStatus());

        return details;
    }

    public Booking updateBookingStatus(Long id, BookingStatus status) {
        logger.info("Updating booking status for ID: {} to {}", id, status);
        Booking booking = getBookingById(id);
        booking.setBookingStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Booking status updated successfully for ID: {}", id);
        return updatedBooking;
    }

    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        if (booking.getBookingStatus() != BookingStatus.CANCELLED) {
            throw new BookingNotFoundException("Only CANCELLED bookings can be deleted");
        }

        logger.info("Deleting booking with ID: {}", id);
        bookingRepository.deleteById(id);
        logger.info("Booking deleted successfully with ID: {}", id);
    }

    public String confirmBooking(ConfirmBookingRequest dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID " + dto.getBookingId()));

        // Check if booking is already confirmed
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new BookingNotFoundException("Booking is already confirmed");
        }

        FlightDTO flight = flightClient.getFlightByNumber(booking.getFlightNumber());
        if (flight == null) {
            throw new BookingNotFoundException("Flight not found");
        }

        int requestedSeats = booking.getPassengers();
        String seatClass = booking.getSeatClass();

        SeatReservationResponse response = flightClient.reserveSeats(booking.getFlightNumber(), requestedSeats, seatClass);
        if (!response.isSuccess()) {
            throw new BookingNotFoundException("Seat reservation failed during confirmation: " + response.getMessage());
        }

        // Update booking status to CONFIRMED
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Send email to user
        UserResponseDTO user = userClient.getUserById(booking.getUserId());
        if (user == null || user.getEmail() == null) {
            throw new BookingNotFoundException("User not found or email missing for userId " + booking.getUserId());
        }

        EmailRequestDTO emailRequest = new EmailRequestDTO();
        emailRequest.setTo(user.getEmail());
        emailRequest.setSubject("Booking Confirmed");
        emailRequest.setMessage("Your booking #" + booking.getId() + " is confirmed.");

        notificationClient.sendEmail(emailRequest);

        return "Booking Confirmed & Email Sent";
    }

}
