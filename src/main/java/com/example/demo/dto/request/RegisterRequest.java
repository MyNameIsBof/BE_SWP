package com.example.demo.dto.request;

import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterRequest {
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

}
