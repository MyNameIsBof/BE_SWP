package com.example.demo;

import com.example.demo.dto.request.NotificationRequest;
import com.example.demo.entity.Notification;
import com.example.demo.enums.NotificationType;
import com.example.demo.mapper.NotificationMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class NotificationSystemTest {

    private final NotificationMapper notificationMapper = Mappers.getMapper(NotificationMapper.class);

    @Test
    void testNotificationRequestMapping() {
        // Given
        NotificationRequest request = new NotificationRequest();
        request.setTitle("Test Notification");
        request.setMessage("This is a test message");
        request.setType(NotificationType.DONATION_REMINDER);

        // When
        Notification notification = notificationMapper.toNotification(request);

        // Then
        assertNotNull(notification);
        assertEquals("Test Notification", notification.getTitle());
        assertEquals("This is a test message", notification.getMessage());
        assertEquals(NotificationType.DONATION_REMINDER, notification.getType());
        assertFalse(notification.isRead()); // Default value should be false
    }

    @Test
    void testNotificationTypes() {
        // Test that all required notification types exist
        assertNotNull(NotificationType.DONATION_REMINDER);
        assertNotNull(NotificationType.EMERGENCY_REQUEST);
        assertNotNull(NotificationType.SYSTEM_ANNOUNCEMENT);
        assertNotNull(NotificationType.APPOINTMENT_CONFIRMATION);
        
        // Test enum count
        assertEquals(4, NotificationType.values().length);
    }
}