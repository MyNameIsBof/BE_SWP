package com.example.demo.dto.request;

import com.example.demo.enums.BloodType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
    private BloodType bloodType;
}
