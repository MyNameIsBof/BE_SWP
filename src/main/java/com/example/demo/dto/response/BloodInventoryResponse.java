package com.example.demo.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class BloodInventoryResponse {
    private Long inventoryId;
    private String institutionId;
    private String bloodType;
    private int unitsAvailable;
    private String address;
    private Date expirationDate;
}
