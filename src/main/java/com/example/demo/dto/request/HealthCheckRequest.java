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
    Double height;
    Double weight;
    Double temperature;
    Double bloodPressure;
    LocalDate checkDate;
    boolean status;
    String reason;
    Long bloodRegisterId;
}
