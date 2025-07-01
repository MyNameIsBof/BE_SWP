package com.example.demo.entity;

import com.example.demo.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    
    @Column(nullable = false)
    String title;
    
    @Column(nullable = false, length = 1000)
    String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NotificationType type;
    
    @Builder.Default
    boolean isRead = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;
    
    LocalDateTime scheduledAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User user;
}