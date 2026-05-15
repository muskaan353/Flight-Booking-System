package com.project.Service.notifi.notification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.Service.notifi.model.Notification;
import com.project.Service.notifi.notificationService.NotificationService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendNotification(@RequestBody Notification notification) throws MessagingException {
        return ResponseEntity.ok(notificationService.sendNotification(notification));
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<Notification>> getNotificationsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.getNotificationsByEmail(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted successfully.");
    }
}
