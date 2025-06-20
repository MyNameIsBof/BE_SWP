package com.example.demo.dto.request;

import com.example.demo.entity.Blood;
import com.example.demo.enums.BloodType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BloodInventoryRequest {
    @NotNull(message = "Không được để trống")
    BloodType bloodType;
    @Min(1)
    double unitsAvailable;
    @NotBlank(message = "Không được để trống")
    Date expirationDate;
}