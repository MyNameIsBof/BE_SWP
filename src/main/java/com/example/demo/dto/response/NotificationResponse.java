package com.example.demo.dto.response;

import com.example.demo.enums.NotificationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class NotificationResponse {
    Long id;
    String title;
    String message;
    NotificationType type;
    boolean isRead;
    LocalDateTime createdAt;
    String recipientName;
    String recipientEmail;
}