package com.project.Service.booking.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.Service.booking.enm.BookingStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    @Column(nullable = false)
    private int passengers;

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private String seatClass; // New field for seat class

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    public Booking() {}

    public Booking(Long id, Long userId, String flightNumber, LocalDate bookingDate,
                   int passengers, double totalPrice, String seatClass, BookingStatus bookingStatus) {
        this.id = id;
        this.userId = userId;
        this.flightNumber = flightNumber;
        this.bookingDate = bookingDate;
        this.passengers = passengers;
        this.totalPrice = totalPrice;
        this.seatClass = seatClass;
        this.bookingStatus = bookingStatus;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
