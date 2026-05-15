package com.project.User.Controller;

import com.project.User.Service.UserService;
import com.project.User.model.LoginRequest;
import com.project.User.model.User;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // User registration
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.register(user);
    }

    // User login & JWT generation
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword());
    }

    // Fetch user profile using JWT from Authorization header
    @GetMapping("/profile")
    public User getUserDetails(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            String username = userService.extractUsername(token); // Extract username from JWT
            return userService.getUserByUsername(username); // Fetch user from DB
        }

        throw new RuntimeException("Missing or invalid Authorization header");
    }
}
