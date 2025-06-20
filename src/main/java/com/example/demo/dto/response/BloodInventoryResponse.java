package com.example.demo.dto.response;

import com.example.demo.enums.BloodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BloodInventoryResponse {
    long id;
    private BloodType bloodType;
    private Long totalUnitsAvailable;
}
