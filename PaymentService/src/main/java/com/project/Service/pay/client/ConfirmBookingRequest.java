package com.project.Service.pay.client;

public class ConfirmBookingRequest {
    private Long bookingId;

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public ConfirmBookingRequest(Long bookingId) {
		super();
		this.bookingId = bookingId;
	}

	public ConfirmBookingRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	

}

