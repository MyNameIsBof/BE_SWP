package com.example.demo.api;

import com.example.demo.dto.request.NotificationRequest;
import com.example.demo.dto.response.NotificationListResponse;
import com.example.demo.dto.response.NotificationResponse;
import com.example.demo.service.NotificationSchedulerService;
import com.example.demo.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing user notifications")
public class NotificationAPI {
    
    private final NotificationService notificationService;
    private final NotificationSchedulerService notificationSchedulerService;
    
    @GetMapping
    @Operation(summary = "Get user's notifications", description = "Retrieve paginated list of notifications for the current user")
    public ResponseEntity<Page<NotificationListResponse>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<NotificationListResponse> notifications = notificationService.getUserNotifications(page, size);
        return ResponseEntity.ok(notifications);
    }
    
    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }
    
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send notification", description = "Send a notification (admin only)")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse notification = notificationService.createNotification(request);
        return ResponseEntity.ok(notification);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete notification", description = "Delete a notification")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted successfully");
    }
    
    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get the count of unread notifications for the current user")
    public ResponseEntity<Long> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/emergency")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send emergency blood request", description = "Send emergency blood request to all users (admin only)")
    public ResponseEntity<String> sendEmergencyRequest(
            @RequestParam String bloodType,
            @RequestParam String message) {
        notificationSchedulerService.sendEmergencyBloodRequest(bloodType, message);
        return ResponseEntity.ok("Emergency blood request sent successfully");
    }
}