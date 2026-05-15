package com.project.flight.dto;

public class SeatReservationResponse {
    private boolean success;
    private String message;

    public SeatReservationResponse() {
    }

    public SeatReservationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
