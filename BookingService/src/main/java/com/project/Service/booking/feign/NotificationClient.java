package com.project.Service.booking.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.project.Service.booking.dto.EmailRequestDTO;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/notify/send-email")
    void sendEmail(@RequestBody EmailRequestDTO emailRequest);
}
