package com.example.demo.service;

import com.example.demo.dto.request.NotificationCreateRequest;
import com.example.demo.dto.response.NotificationResponse;
import com.example.demo.dto.response.UnreadCountResponse;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.enums.NotificationType;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.mapper.NotificationMapper;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AuthenticationRepository authenticationRepository;

    @Transactional
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        User recipient = authenticationRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new GlobalException("Recipient not found"));
        
        Notification notification = notificationMapper.toNotification(request);
        notification.setRecipient(recipient);
        
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(savedNotification);
    }

    @Transactional
    public NotificationResponse createNotification(String title, String message, NotificationType type, User recipient) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .recipient(recipient)
                .isRead(false)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(savedNotification);
    }

    public List<NotificationResponse> getNotificationsByUser(User user) {
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)//trả về danh sách thông báo của người dùng theo ID
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GlobalException("Notification not found"));
        
        // Check if current user is the recipient
        if (notification.getRecipient().getId() != currentUser.getId()) {
            throw new GlobalException("Unauthorized to modify this notification");
        }
        
        notification.setRead(true);
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(savedNotification);
    }

    @Transactional
    public void deleteNotification(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GlobalException("Notification not found"));
        
        // Check if current user is the recipient
        if (notification.getRecipient().getId() != currentUser.getId()) {
            throw new GlobalException("Unauthorized to delete this notification");
        }
        
        notificationRepository.delete(notification);
    }

    public UnreadCountResponse getUnreadCount(User user) {
        Long count = notificationRepository.countUnreadByRecipientId(user.getId());
        return UnreadCountResponse.builder()
                .unreadCount(count)
                .build();
    }

    public UnreadCountResponse getUnreadCountByUserId(Long userId) {
        Long count = notificationRepository.countUnreadByRecipientId(userId);
        return UnreadCountResponse.builder()
                .unreadCount(count)
                .build();
    }

    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user.getId());
    }

    @Transactional
    public void markAllAsReadForUser(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    // Helper methods for common notification types
    @Transactional
    public void createBloodRequestNotification(User recipient, String details) {
        createNotification(
                "New Blood Request",
                "A new blood donation request has been created. " + details,
                NotificationType.BLOOD_REQUEST,
                recipient
        );
    }

    @Transactional
    public void createEmergencyRequestNotification(User recipient, String details) {
        createNotification(
                "Emergency Blood Request",
                "URGENT: Emergency blood donation needed! " + details,
                NotificationType.EMERGENCY_REQUEST,
                recipient
        );
    }

    @Transactional
    public void createDonationCompletedNotification(User recipient, String details) {
        createNotification(
                "Donation Completed",
                "Your blood donation has been completed successfully. " + details,
                NotificationType.DONATION_COMPLETED,
                recipient
        );
    }

    @Transactional
    public void createDonationReminderNotification(User recipient, String details) {
        createNotification(
                "Donation Reminder",
                "Reminder: Your scheduled blood donation is coming up. " + details,
                NotificationType.BLOOD_DONATION_REMINDER,
                recipient
        );
    }

    @Transactional
    public void createSystemAnnouncementNotification(User recipient, String title, String message) {
        createNotification(
                title,
                message,
                NotificationType.SYSTEM_ANNOUNCEMENT,
                recipient
        );
    }
}