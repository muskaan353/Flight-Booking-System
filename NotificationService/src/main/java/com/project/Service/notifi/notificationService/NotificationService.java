package com.project.Service.notifi.notificationService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.Service.notifi.email.service.EmailService;
import com.project.Service.notifi.model.Notification;
import com.project.Service.notifi.repository.NotificationRepository;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationRepository notificationRepository;

    // Send notification and email
    public String sendNotification(Notification notification) throws MessagingException {
        if (notification.getEmail() == null || notification.getEmail().trim().isEmpty()) {
            logger.warn("Failed to send notification: Email is required.");
            return "Email is required.";
        }
        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            logger.warn("Failed to send notification: Message cannot be empty.");
            return "Message cannot be empty.";
        }

        // Send email
        emailService.sendBookingConfirmation(notification.getEmail(), notification.getMessage());

        // Save notification after successful email
        notificationRepository.save(notification);
        logger.info("Notification sent and saved for email: {}", notification.getEmail());

        return "Email Notification Sent!";
    }

    // Get all notifications
    public List<Notification> getAllNotifications() {
        logger.info("Fetching all notifications.");
        return notificationRepository.findAll();
    }

    // Get notifications by email
    public List<Notification> getNotificationsByEmail(String email) {
        logger.info("Fetching notifications for email: {}", email);
        return notificationRepository.findByEmail(email);
    }

    // Delete notification by ID
    public void deleteNotification(Long id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            notificationRepository.deleteById(id);
            logger.info("Deleted notification with ID: {}", id);
        } else {
            logger.warn("Attempted to delete non-existent notification with ID: {}", id);
            throw new RuntimeException("Notification with ID " + id + " not found.");
        }
    }
}
