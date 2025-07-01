package com.example.demo.mapper;

import com.example.demo.dto.request.NotificationRequest;
import com.example.demo.dto.response.NotificationListResponse;
import com.example.demo.dto.response.NotificationResponse;
import com.example.demo.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Notification toNotification(NotificationRequest request);
    
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    NotificationResponse toNotificationResponse(Notification notification);
    
    NotificationListResponse toNotificationListResponse(Notification notification);
}