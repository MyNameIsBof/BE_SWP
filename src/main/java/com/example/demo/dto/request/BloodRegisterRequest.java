package com.example.demo.dto.request;

import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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
public class BloodRegisterRequest {
    Gender gender;
    LocalDate birthdate;
    double height;
    double weight;
    LocalDate lastDonation;
    String medicalHistory;
    @Enumerated(EnumType.STRING)
    BloodType bloodType;


    LocalDate wantedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(example = "10:30:00")
    LocalTime wantedHour;
    String emergencyName;
    String emergencyPhone;
}
