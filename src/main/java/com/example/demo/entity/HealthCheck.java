package com.example.demo.entity;

import com.example.demo.enums.BloodRegisterStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class HealthCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String fullName;
    double height;
    double weight;
    double temperature;
    double bloodPressure;
    String medicalHistory;
    LocalDate checkDate;
    String staffName;
    @Enumerated(EnumType.STRING)
    BloodRegisterStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_register_id")
    @JsonIgnore
    BloodRegister bloodRegister;

}
