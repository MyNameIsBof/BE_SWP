package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @Email
    @NotBlank(message = "Không được để trống")
    public String email;
<<<<<<< HEAD
<<<<<<< main
=======
    @NotBlank(message = "Không được để trống")
//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
//            message = "Mật khẩu phải 8-20 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt"
//    )
>>>>>>> local
=======
    @NotBlank(message = "Không được để trống")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Mật khẩu phải 8-20 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
>>>>>>> 5739803dc40f1a81f287b1403629a4fe6a53de04
    public String password;
}
