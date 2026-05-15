package com.project.Service.notifi.servicetest;

import com.project.Service.notifi.email.service.EmailService;
import com.project.Service.notifi.model.Notification;
import com.project.Service.notifi.notificationService.NotificationService;
import com.project.Service.notifi.repository.NotificationRepository;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationRepository notificationRepository;

    private Notification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        notification = new Notification();
        notification.setId(1L);
        notification.setEmail("test@example.com");
        notification.setMessage("Your booking is confirmed!");
    }

    @Test
    void testSendNotification_Success() throws MessagingException {
        doNothing().when(emailService).sendBookingConfirmation(anyString(), anyString());
        when(notificationRepository.save(notification)).thenReturn(notification);

        String result = notificationService.sendNotification(notification);

        assertEquals("Email Notification Sent!", result);
        verify(emailService, times(1)).sendBookingConfirmation("test@example.com", "Your booking is confirmed!");
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testSendNotification_MissingEmail() throws MessagingException {
        notification.setEmail("");

        String result = notificationService.sendNotification(notification);

        assertEquals("Email is required.", result);
        verify(emailService, never()).sendBookingConfirmation(any(), any());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testSendNotification_MissingMessage() throws MessagingException {
        notification.setMessage("");

        String result = notificationService.sendNotification(notification);

        assertEquals("Message cannot be empty.", result);
        verify(emailService, never()).sendBookingConfirmation(any(), any());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testGetAllNotifications() {
        List<Notification> list = Arrays.asList(notification);
        when(notificationRepository.findAll()).thenReturn(list);

        List<Notification> result = notificationService.getAllNotifications();

        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void testGetNotificationsByEmail() {
        List<Notification> list = Arrays.asList(notification);
        when(notificationRepository.findByEmail("test@example.com")).thenReturn(list);

        List<Notification> result = notificationService.getNotificationsByEmail("test@example.com");

        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testDeleteNotification_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        notificationService.deleteNotification(1L);

        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNotification_NotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            notificationService.deleteNotification(999L)
        );

        assertEquals("Notification with ID 999 not found.", exception.getMessage());
        verify(notificationRepository, never()).deleteById(999L);
    }
}
