package com.project.Service.notifi.email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // Send booking confirmation email
    public void sendBookingConfirmation(String toEmail, String message) throws MessagingException {
        sendEmail(toEmail, "Booking Confirmation", message);
    }

    // Send  text email
    private void sendEmail(String toEmail, String subject, String message) throws MessagingException {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
        logger.info("Email sent to: " + toEmail);
    }


}


