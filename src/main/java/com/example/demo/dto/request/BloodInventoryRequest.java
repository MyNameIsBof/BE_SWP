package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class BloodInventoryRequest {
    @NotBlank
    private String institutionId;
    @NotBlank
    private String bloodType;
    @Min(1)
    private int unitsAvailable;
    private String address;
    private Date expirationDate;
}