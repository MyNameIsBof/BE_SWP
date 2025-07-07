package com.example.demo.service;

import com.example.demo.dto.request.NotificationCreateRequest;
import com.example.demo.dto.response.NotificationResponse;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.enums.NotificationType;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import com.example.demo.mapper.NotificationMapper;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private AuthenticationRepository authenticationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification testNotification;
    private NotificationCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .bloodType(BloodType.A_POSITIVE)
                .role(Role.MEMBER)
                .build();

        testNotification = Notification.builder()
                .id(1L)
                .title("Test Notification")
                .message("Test message")
                .type(NotificationType.BLOOD_REQUEST)
                .recipient(testUser)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = NotificationCreateRequest.builder()
                .title("Test Notification")
                .message("Test message")
                .type(NotificationType.BLOOD_REQUEST)
                .recipientId(1L)
                .build();
    }

    @Test
    void createNotification_ShouldReturnNotificationResponse_WhenValidRequest() {
        // Arrange
        when(authenticationRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(notificationMapper.toNotification(testRequest)).thenReturn(testNotification);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(notificationMapper.toNotificationResponse(testNotification))
                .thenReturn(NotificationResponse.builder()
                        .id(1L)
                        .title("Test Notification")
                        .message("Test message")
                        .type(NotificationType.BLOOD_REQUEST)
                        .isRead(false)
                        .recipientName("Test User")
                        .recipientEmail("test@example.com")
                        .build());

        // Act
        NotificationResponse result = notificationService.createNotification(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Notification", result.getTitle());
        assertEquals("Test message", result.getMessage());
        assertEquals(NotificationType.BLOOD_REQUEST, result.getType());
        assertFalse(result.isRead());
        assertEquals("Test User", result.getRecipientName());
        assertEquals("test@example.com", result.getRecipientEmail());

        verify(authenticationRepository).findById(1L);
        verify(notificationMapper).toNotification(testRequest);
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).toNotificationResponse(testNotification);
    }

    @Test
    void createNotificationDirectly_ShouldReturnNotificationResponse_WhenValidParameters() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(notificationMapper.toNotificationResponse(testNotification))
                .thenReturn(NotificationResponse.builder()
                        .id(1L)
                        .title("Direct Notification")
                        .message("Direct message")
                        .type(NotificationType.EMERGENCY_REQUEST)
                        .isRead(false)
                        .build());

        // Act
        NotificationResponse result = notificationService.createNotification(
                "Direct Notification",
                "Direct message",
                NotificationType.EMERGENCY_REQUEST,
                testUser
        );

        // Assert
        assertNotNull(result);
        assertEquals("Direct Notification", result.getTitle());
        assertEquals("Direct message", result.getMessage());
        assertEquals(NotificationType.EMERGENCY_REQUEST, result.getType());
        assertFalse(result.isRead());

        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).toNotificationResponse(any(Notification.class));
    }

    @Test
    void createBloodRequestNotification_ShouldCreateCorrectNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(notificationMapper.toNotificationResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder()
                        .title("New Blood Request")
                        .type(NotificationType.BLOOD_REQUEST)
                        .build());

        // Act
        notificationService.createBloodRequestNotification(testUser, "Blood type A+");

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).toNotificationResponse(any(Notification.class));
    }

    @Test
    void createEmergencyRequestNotification_ShouldCreateCorrectNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(notificationMapper.toNotificationResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder()
                        .title("Emergency Blood Request")
                        .type(NotificationType.EMERGENCY_REQUEST)
                        .build());

        // Act
        notificationService.createEmergencyRequestNotification(testUser, "Urgent blood needed");

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).toNotificationResponse(any(Notification.class));
    }

    @Test
    void createDonationCompletedNotification_ShouldCreateCorrectNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(notificationMapper.toNotificationResponse(any(Notification.class)))
                .thenReturn(NotificationResponse.builder()
                        .title("Donation Completed")
                        .type(NotificationType.DONATION_COMPLETED)
                        .build());

        // Act
        notificationService.createDonationCompletedNotification(testUser, "Successfully donated");

        // Assert
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationMapper).toNotificationResponse(any(Notification.class));
    }

    @Test
    void getUnreadCountByUserId_ShouldReturnCorrectCount() {
        // Arrange
        when(notificationRepository.countUnreadByRecipientId(1L)).thenReturn(5L);

        // Act
        var result = notificationService.getUnreadCountByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getUnreadCount());
        verify(notificationRepository).countUnreadByRecipientId(1L);
    }
}