package com.example.demo.dto.request;

import com.example.demo.enums.BloodType;
import com.example.demo.enums.Gender;
import com.example.demo.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserRequest {
@NotBlank(message = "Họ và tên không được để trống") // Fullname cannot be empty
    @Pattern(regexp = "^[a-zA-ZÀ-ỹ\\s]{2,50}$", message = "Họ và tên phải từ 2-50 ký tự và không chứa ký tự đặc biệt")
    String fullName;

    @NotBlank(message = "Số điện thoại không được để trống") // Phone cannot be empty
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    String phone;

    @NotBlank(message = "Địa chỉ không được để trống") // Address cannot be empty
    String address;

    @NotNull(message = "Giới tính không được để trống") // Gender cannot be empty
    @Enumerated(EnumType.STRING)
    Gender gender;

    @NotNull(message = "Ngày sinh không được để trống") // Birthdate cannot be empty
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    LocalDate birthdate;

    @Min(value = 100, message = "Chiều cao phải lớn hơn hoặc bằng 100cm")
    @Max(value = 250, message = "Chiều cao phải nhỏ hơn hoặc bằng 250cm")
    double height; // Chiều cao (cm)

    @Min(value = 40, message = "Cân nặng phải lớn hơn hoặc bằng 40kg")
    @Max(value = 200, message = "Cân nặng phải nhỏ hơn hoặc bằng 200kg")
    double weight; // Cân nặng (kg)

    @Past(message = "Ngày hiến máu gần nhất phải là ngày trong quá khứ")
    LocalDate lastDonation; // Ngày hiến máu gần nhất

    String medicalHistory; // Tiền sử bệnh

    @NotBlank(message = "Tên người liên hệ khẩn cấp không được để trống")
    String emergencyName; // Tên người liên hệ khẩn cấp

    @NotBlank(message = "Số điện thoại liên hệ khẩn cấp không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại khẩn cấp không hợp lệ")
    String emergencyPhone; // Số điện thoại liên hệ khẩn cấp

    @NotNull(message = "Nhóm máu không được để trống") // Blood type cannot be empty
    @Enumerated(EnumType.STRING)
    BloodType bloodType; // Nhóm máu
}
