package com.project.flight.model;

import com.project.flight.enm.SeatClass;
import jakarta.persistence.*;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatClass seatClass;

    private int totalSeats;
    private int bookedSeats;
    private double price;

    public int getAvailableSeats() {
        return totalSeats - bookedSeats;
    }

    public double getDynamicFare() {
        double occupancyRate = (double) bookedSeats / totalSeats;
        if (occupancyRate > 0.8) {
            return price * 1.2;
        } else if (occupancyRate < 0.5) {
            return price * 0.9;
        }
        return price;
    }

    public boolean bookSeats(int numberOfSeats) {
        if (numberOfSeats <= getAvailableSeats()) {
            this.bookedSeats += numberOfSeats;
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

	public void setAvailableSeats(int i) {
		// TODO Auto-generated method stub
		
	}

	public void setDynamicFare(double d) {
		// TODO Auto-generated method stub
		
	}
}
