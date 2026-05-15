package com.project.Service.booking.dto;

import java.util.Map;

public class FlightDTO {

    private String flightNumber;
    private String airline;
    private String source;
    private String destination;
    private String departureDate;
    private String departureTime;
    private String arrivalTime;
    private Map<String, Integer> availableSeatsPerClass;
    private Map<String, Double> pricePerClass;

    public FlightDTO() {}

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

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
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
}
