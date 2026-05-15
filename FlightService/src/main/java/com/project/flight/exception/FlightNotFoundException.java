package com.project.flight.exception;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(String flightNumber) {
        super("Flight with flight number " + flightNumber + " not found.");
    }
}