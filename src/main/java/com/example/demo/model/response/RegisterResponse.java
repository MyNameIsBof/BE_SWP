package com.example.demo.model.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterResponse {
    String message;
    String email;
    String fullName;
    String phoneNumber;
    String address;
    String location;
    String bloodType;
    Date lastDonationDate;
    String role;
}
