package com.example.demo.dto.response;

import com.example.demo.enums.BloodRegisterStatus;
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
    LocalDate wantedDate;
    LocalTime wantedHour;
    BloodRegisterStatus status;
}
