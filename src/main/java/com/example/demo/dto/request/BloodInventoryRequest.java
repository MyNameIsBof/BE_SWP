package com.example.demo.dto.request;

import com.example.demo.enums.BloodType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BloodInventoryRequest {
    @NotNull(message = "Blood type không được để trống")
    BloodType bloodType;
    @Min(value = 1, message = "Units available phải lớn hơn hoặc bằng 1")
    int unitsAvailable;
    @NotBlank(message = "Address không được để trống")
    String address;
    @Future(message = "Expiration date phải là ngày trong tương lai")
    @NotNull(message = "Expiration date không được để trống")
    Date expirationDate;
}