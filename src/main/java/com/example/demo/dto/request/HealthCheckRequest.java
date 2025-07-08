package com.example.demo.dto.request;

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
public class HealthCheckRequest {
    double height;
    double weight;
    double temperature;
    double bloodPressure;
    LocalDate checkDate;
    @Enumerated(EnumType.STRING)
    BloodRegisterStatus status;
    Long bloodRegisterId;
}
