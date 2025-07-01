package com.example.demo.service;

import com.example.demo.dto.request.EmailDetail;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {
    
    private final EmailService emailService;
    
    public void sendNotificationEmail(Notification notification) {
        User user = notification.getUser();
        
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setRecipient(user.getEmail());
            emailDetail.setSubject(getEmailSubject(notification));
            
            // The existing EmailService uses a template, so we'll let it handle the message formatting
            emailService.sendMail(emailDetail);
        }
    }
    
    public void sendDonationReminderEmail(User user) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setRecipient(user.getEmail());
            emailDetail.setSubject("Blood Donation Reminder - You're Eligible to Donate Again");
            
            emailService.sendMail(emailDetail);
        }
    }
    
    public void sendEmergencyRequestEmail(User user, String bloodType, String message) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setRecipient(user.getEmail());
            emailDetail.setSubject("URGENT: Emergency Blood Request - " + bloodType);
            
            emailService.sendMail(emailDetail);
        }
    }
    
    public void sendAppointmentConfirmationEmail(User user, String appointmentDetails) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setRecipient(user.getEmail());
            emailDetail.setSubject("Appointment Confirmation - Blood Donation Center");
            
            emailService.sendMail(emailDetail);
        }
    }
    
    private String getEmailSubject(Notification notification) {
        return switch (notification.getType()) {
            case DONATION_REMINDER -> "Blood Donation Reminder";
            case EMERGENCY_REQUEST -> "URGENT: Emergency Blood Request";
            case APPOINTMENT_CONFIRMATION -> "Appointment Confirmation";
            case SYSTEM_ANNOUNCEMENT -> "System Announcement";
        };
    }
}