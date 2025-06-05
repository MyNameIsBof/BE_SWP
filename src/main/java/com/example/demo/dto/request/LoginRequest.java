package com.example.demo.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    public String email;
<<<<<<< main
=======
    @NotBlank(message = "Không được để trống")
//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
//            message = "Mật khẩu phải 8-20 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt"
//    )
>>>>>>> local
    public String password;
}
