package com.project.User;



import com.project.User.Repository.UserRepository;
import com.project.User.Service.UserService;
import com.project.User.enm.Role;
import com.project.User.exception.UserNotFoundException;
import com.project.User.model.User;
import com.project.User.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUsername("john");
        testUser.setPassword("pass123");
        testUser.setRole(Role.USER);
    }

    @Test
    void testRegister_Success() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        String response = userService.register(testUser);

        assertEquals("User registered successfully", response);
        verify(repository, times(1)).save(testUser);
        assertEquals("encodedPass", testUser.getPassword());
    }

    @Test
    void testLogin_Success() {
        when(repository.findByUsername("john")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("pass123", "pass123")).thenReturn(true);
        when(jwtUtil.generateToken("john", "USER")).thenReturn("mockedToken");

        String token = userService.login("john", "pass123");

        assertEquals("mockedToken", token);
    }

    @Test
    void testLogin_UserNotFound() {
        when(repository.findByUsername("invalid")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.login("invalid", "anyPass");
        });
    }

    @Test
    void testGetUserByUsername_Success() {
        when(repository.findByUsername("john")).thenReturn(Optional.of(testUser));

        User user = userService.getUserByUsername("john");

        assertEquals("john", user.getUsername());
    }

    @Test
    void testGetUserByUsername_NotFound() {
        when(repository.findByUsername("notExist")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserByUsername("notExist");
        });
    }

    @Test
    void testExtractUsername() {
        when(jwtUtil.extractUsername("token")).thenReturn("john");

        String result = userService.extractUsername("token");

        assertEquals("john", result);
    }
}
