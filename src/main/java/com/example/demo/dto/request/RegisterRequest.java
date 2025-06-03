package com.example.demo.dto.request;

import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
     @NotBlank(message = "Không được để trống")
     public String full_name;
     @Email
     @NotBlank(message = "Không được để trống")
     public String email;
     @NotBlank(message = "Không được để trống")
     @Pattern(
             regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
             message = "Mật khẩu phải 8-20 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt"
     )
     public String password;
     @NotBlank(message = "Không được để trống")
     @Pattern(regexp = "^(0[3|5|7|8|9][0-9]{8}|\\+84[3|5|7|8|9][0-9]{8})$", message = "Số điện thoại không hợp lệ")
     public String phone;
     @NotBlank(message = "Không được để trống")
     public String address;
     @NotBlank(message = "Không được để trống")
     @Enumerated(EnumType.STRING)
     public BloodType blood_type;


}
