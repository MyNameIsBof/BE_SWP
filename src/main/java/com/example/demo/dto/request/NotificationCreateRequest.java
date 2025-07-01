package com.example.demo.dto.request;

import com.example.demo.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class NotificationCreateRequest {
    @NotBlank(message = "Title cannot be blank")
    String title;

    @NotBlank(message = "Message cannot be blank")
    String message;

    @NotNull(message = "Type cannot be null")
    NotificationType type;

    @NotNull(message = "Recipient ID cannot be null")
    Long recipientId;
}