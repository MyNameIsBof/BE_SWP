package com.example.demo.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class BloodInventoryResponse {
    private String bloodType;
    private Long totalUnitsAvailable;
}
