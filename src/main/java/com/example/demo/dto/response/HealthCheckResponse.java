package com.example.demo.dto.response;

import com.example.demo.enums.BloodRegisterStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class HealthCheckResponse {
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
    Long bloodRegisterId;
}
