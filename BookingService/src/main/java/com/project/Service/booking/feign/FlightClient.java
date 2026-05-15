package com.project.Service.booking.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.Service.booking.dto.FlightDTO;
import com.project.Service.booking.dto.SeatReservationResponse;

@FeignClient(name = "FLIGHT-SERVICE", url = "http://localhost:8081")
public interface FlightClient {

    @GetMapping("/flights/flight/{flightNumber}")
    FlightDTO getFlightByNumber(@PathVariable("flightNumber") String flightNumber);

    @PutMapping("/flights/reserveSeats")
    SeatReservationResponse reserveSeats(@RequestParam("flightNumber") String flightNumber,
                                         @RequestParam("passengers") int passengers,
                                         @RequestParam("seatClass") String seatClass);
}
