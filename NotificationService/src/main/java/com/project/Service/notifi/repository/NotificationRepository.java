package com.project.Service.notifi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.Service.notifi.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByEmail(String email);
}
