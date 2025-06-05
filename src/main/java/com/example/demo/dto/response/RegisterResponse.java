package com.example.demo.dto.response;


import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterResponse {
    public String full_name;
    public String email;
    public String password;
    public String phone;
    public String address;
    public String location;
    @Enumerated(EnumType.STRING)
    public BloodType blood_type;
    public Date last_donation;
    @Enumerated(EnumType.STRING)
    public Role role;
    String token;
}
