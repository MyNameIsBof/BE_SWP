package com.example.demo.model.request;

import com.example.demo.enums.Role;
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
     String fullName;
     String email;
     String password;
     String phoneNumber;
     String address;
     String location;
     Role role;
     String bloodType;
    Date lastDonationDate;

}
