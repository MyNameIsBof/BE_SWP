package com.example.demo.dto.response;

import com.example.demo.enums.BloodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloodInventorySummaryResponse {
    private BloodType bloodType;
    private Long totalUnitsAvailable;
}