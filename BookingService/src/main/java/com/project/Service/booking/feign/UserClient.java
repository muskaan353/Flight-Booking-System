package com.project.Service.booking.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.Service.booking.dto.UserResponseDTO;



@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/user/{id}")
    UserResponseDTO getUserById(@PathVariable("id") Long userId);
}
