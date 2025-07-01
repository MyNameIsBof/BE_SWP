package com.example.demo.service;

import com.example.demo.dto.request.NotificationRequest;
import com.example.demo.dto.response.NotificationListResponse;
import com.example.demo.dto.response.NotificationResponse;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.enums.NotificationType;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.exception.exceptions.ResourceNotFoundException;
import com.example.demo.mapper.NotificationMapper;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AuthenticationService authenticationService;
    private final AuthenticationRepository authenticationRepository;
    
    public Page<NotificationListResponse> getUserNotifications(int page, int size) {
        User currentUser = authenticationService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);
        
        return notifications.map(notificationMapper::toNotificationListResponse);
    }
    
    @Transactional
    public void markAsRead(Long notificationId) {
        User currentUser = authenticationService.getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        // Check if notification belongs to current user
        if (notification.getUser().getId() != currentUser.getId()) {
            throw new AuthenticationException("You don't have permission to access this notification");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        User currentUser = authenticationService.getCurrentUser();
        
        // Only admin can send notifications to specific users
        if (request.getUserId() != null && currentUser.getRole() != Role.ADMIN) {
            throw new AuthenticationException("Only admin can send notifications to specific users");
        }
        
        // Determine target user
        User targetUser = currentUser;
        if (request.getUserId() != null) {
            targetUser = authenticationRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        
        Notification notification = notificationMapper.toNotification(request);
        notification.setUser(targetUser);
        
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(savedNotification);
    }
    
    @Transactional
    public void deleteNotification(Long notificationId) {
        User currentUser = authenticationService.getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        // Check if notification belongs to current user or user is admin
        if (notification.getUser().getId() != currentUser.getId() && 
            currentUser.getRole() != Role.ADMIN) {
            throw new AuthenticationException("You don't have permission to delete this notification");
        }
        
        notificationRepository.delete(notification);
    }
    
    public long getUnreadCount() {
        User currentUser = authenticationService.getCurrentUser();
        return notificationRepository.countUnreadByUserId(currentUser.getId());
    }
    
    // Internal methods for system-generated notifications
    @Transactional
    public void createDonationReminder(User user) {
        if (user == null) {
            log.warn("Cannot create donation reminder - user is null");
            return;
        }
        
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Donation Reminder");
        request.setMessage("You are now eligible to donate blood again. Your last donation was more than 90 days ago.");
        request.setType(NotificationType.DONATION_REMINDER);
        request.setUserId(user.getId());
        
        Notification notification = notificationMapper.toNotification(request);
        notification.setUser(user);
        notificationRepository.save(notification);
    }
    
    @Transactional
    public void createAppointmentConfirmation(User user, String appointmentDetails) {
        if (user == null) {
            log.warn("Cannot create appointment confirmation - user is null");
            return;
        }
        
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Appointment Confirmed");
        request.setMessage("Your blood donation/receive appointment has been confirmed: " + 
            (appointmentDetails != null ? appointmentDetails : "No details provided"));
        request.setType(NotificationType.APPOINTMENT_CONFIRMATION);
        request.setUserId(user.getId());
        
        Notification notification = notificationMapper.toNotification(request);
        notification.setUser(user);
        notificationRepository.save(notification);
    }
    
    @Transactional
    public void createEmergencyRequest(String bloodType, String message) {
        // Send to all users with compatible blood types
        List<User> eligibleUsers = authenticationRepository.findAll(); // Could be filtered by blood type compatibility
        
        for (User user : eligibleUsers) {
            NotificationRequest request = new NotificationRequest();
            request.setTitle("Emergency Blood Request");
            request.setMessage("URGENT: " + message + " Blood type needed: " + bloodType);
            request.setType(NotificationType.EMERGENCY_REQUEST);
            request.setUserId(user.getId());
            
            Notification notification = notificationMapper.toNotification(request);
            notification.setUser(user);
            notificationRepository.save(notification);
        }
    }
}