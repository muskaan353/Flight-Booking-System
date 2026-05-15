package com.project.Service.pay.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "BOOKING-SERVICE" , url="http://localhost:8083")
public interface BookingClient {

    @PostMapping("/bookings/confirm")
    String confirmBooking(@RequestBody ConfirmBookingRequest request);
}
