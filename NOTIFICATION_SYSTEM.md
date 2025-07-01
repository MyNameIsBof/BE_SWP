# Notification System Documentation

## Overview
This notification system provides comprehensive notification management for the blood donation application, including automatic reminders, emergency requests, and appointment confirmations.

## Features

### 1. Notification Types
- **DONATION_REMINDER**: Automated reminders when users become eligible to donate again
- **EMERGENCY_REQUEST**: Urgent blood requests sent to all users
- **SYSTEM_ANNOUNCEMENT**: General announcements from administrators
- **APPOINTMENT_CONFIRMATION**: Confirmation notifications for approved appointments

### 2. Core Components

#### Entities
- `Notification`: Main notification entity with fields for id, userId, title, message, type, isRead, createdAt, scheduledAt
- `NotificationType`: Enum for different notification categories

#### Services
- `NotificationService`: Core business logic for CRUD operations and user notifications
- `EmailNotificationService`: Integration with existing email system
- `NotificationSchedulerService`: Automated scheduled tasks for reminders

#### API Endpoints
- `GET /api/notifications`: Get paginated user notifications
- `PUT /api/notifications/{id}/read`: Mark notification as read
- `POST /api/notifications/send`: Send notification (admin only)
- `DELETE /api/notifications/{id}`: Delete notification
- `GET /api/notifications/unread-count`: Get unread notification count
- `POST /api/notifications/emergency`: Send emergency blood request (admin only)

### 3. Integration Points

#### Blood Registration System
- Sends appointment confirmation when blood donation requests are approved
- Integrates with existing BloodRegisterService

#### Blood Receive System  
- Sends appointment confirmation when blood receive requests are approved
- Integrates with existing BloodReceiveService

#### Automated Reminders
- Daily scheduled job (9 AM) to check donation eligibility
- Uses BloodDonationConfig for recovery period (90 days) and reminder threshold (7 days)
- Sends both in-app notifications and email notifications

#### Email Integration
- Leverages existing EmailService and JavaMailSender
- Uses existing Thymeleaf email templates
- Sends notifications for all major events

### 4. Security
- Role-based access control using existing Spring Security setup
- Only admins can send notifications to other users
- Users can only manage their own notifications
- Emergency requests are admin-only

### 5. Database
- Uses existing JPA/Hibernate setup with MySQL
- Automatic schema creation/update via `spring.jpa.hibernate.ddl-auto=update`
- Pagination support for notification lists

### 6. Configuration
- Scheduling enabled via `@EnableScheduling`
- Uses existing BloodDonationConfig for timing parameters
- Leverages existing email configuration

## Usage Examples

### Getting User Notifications
```
GET /api/notifications?page=0&size=10
```

### Marking as Read
```
PUT /api/notifications/123/read
```

### Sending Emergency Request (Admin)
```
POST /api/notifications/emergency?bloodType=A+&message=Urgent need for surgery
```

### Sending Custom Notification (Admin)
```
POST /api/notifications/send
{
  "title": "System Maintenance",
  "message": "System will be down for maintenance tonight",
  "type": "SYSTEM_ANNOUNCEMENT",
  "userId": 123
}
```