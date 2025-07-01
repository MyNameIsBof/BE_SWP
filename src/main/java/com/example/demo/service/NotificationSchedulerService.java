package com.example.demo.service;

import com.example.demo.config.BloodDonationConfig;
import com.example.demo.entity.User;
import com.example.demo.repository.AuthenticationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSchedulerService {
    
    private final AuthenticationRepository authenticationRepository;
    private final NotificationService notificationService;
    private final EmailNotificationService emailNotificationService;
    private final BloodDonationConfig bloodDonationConfig;
    
    @Scheduled(cron = "0 0 9 * * *") // Run daily at 9 AM
    public void sendDonationReminders() {
        log.info("Starting daily donation reminder job");
        
        try {
            LocalDate today = LocalDate.now();
            LocalDate eligibilityDate = today.minusDays(bloodDonationConfig.getRecoveryPeriodDays());
            LocalDate reminderThreshold = today.plusDays(bloodDonationConfig.getReminderBeforeDayCount());
            
            // Find users who are eligible to donate again
            List<User> allUsers = authenticationRepository.findAll();
            
            for (User user : allUsers) {
                if (user.getLastDonation() != null) {
                    LocalDate nextEligibleDate = user.getLastDonation().plusDays(bloodDonationConfig.getRecoveryPeriodDays());
                    
                    // Check if user becomes eligible within reminder threshold
                    if (nextEligibleDate.isBefore(reminderThreshold) && nextEligibleDate.isAfter(today)) {
                        log.info("Sending donation reminder to user: {}", user.getEmail());
                        
                        // Create notification
                        notificationService.createDonationReminder(user);
                        
                        // Send email
                        emailNotificationService.sendDonationReminderEmail(user);
                    }
                }
            }
            
            log.info("Completed daily donation reminder job");
        } catch (Exception e) {
            log.error("Error in donation reminder job", e);
        }
    }
    
    public void sendEmergencyBloodRequest(String bloodType, String message) {
        log.info("Sending emergency blood request for blood type: {}", bloodType);
        
        try {
            // Create notifications for all users
            notificationService.createEmergencyRequest(bloodType, message);
            
            // Send emails to all eligible users
            List<User> allUsers = authenticationRepository.findAll();
            for (User user : allUsers) {
                emailNotificationService.sendEmergencyRequestEmail(user, bloodType, message);
            }
            
            log.info("Emergency blood request notifications sent successfully");
        } catch (Exception e) {
            log.error("Error sending emergency blood request", e);
        }
    }
}