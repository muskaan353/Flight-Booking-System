package com.project.flight.dto;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
public class FlightDetailsDTO {
    private String flightNumber;
    private String airline;
    private String source;
    private String destination;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String duration;
    private Map<String, Integer> availableSeatsPerClass; // Economy, Business, etc.
    private Map<String, Double> pricePerClass; // Economy: 2000.0, Business: 5000.0
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	public String getAirline() {
		return airline;
	}
	public void setAirline(String airline) {
		this.airline = airline;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public LocalDate getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate = departureDate;
	}
	public LocalTime getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(LocalTime departureTime) {
		this.departureTime = departureTime;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public Map<String, Integer> getAvailableSeatsPerClass() {
		return availableSeatsPerClass;
	}
	public void setAvailableSeatsPerClass(Map<String, Integer> availableSeatsPerClass) {
		this.availableSeatsPerClass = availableSeatsPerClass;
	}
	public Map<String, Double> getPricePerClass() {
		return pricePerClass;
	}
	public void setPricePerClass(Map<String, Double> pricePerClass) {
		this.pricePerClass = pricePerClass;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
    
}
