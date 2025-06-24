package com.example.demo.dto.response;

import com.example.demo.enums.BloodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BloodInventoryResponse {
    BloodType bloodType;
    float unitsAvailable;
}