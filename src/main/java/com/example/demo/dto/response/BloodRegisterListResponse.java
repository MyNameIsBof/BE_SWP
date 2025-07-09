package com.example.demo.dto.response;

import com.example.demo.entity.Blood;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.HealthCheckStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BloodRegisterListResponse {
    long id;
    String fullName;
    LocalDate wantedDate;
    LocalTime wantedHour;
    BloodRegisterStatus status;
    BloodType bloodType;
    float unit;
    boolean healthCheckStatus;

}
