package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class BloodInventoryRequest {
    @NotBlank(message = "Không được để trống")
    private String institutionId;
    @NotBlank(message = "Không được để trống")
    private String bloodType;
    @Min(1)
    private int unitsAvailable;
    @NotBlank(message = "Không được để trống")
    private String address;
    private Date expirationDate;
}