package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CertififcateRequest {
    @NotNull(message = "Ngày cấp không được để trống")
    LocalDate issueDate;

    @NotNull(message = "Tên người hiến máu không được để trống")
    String donorName;

    @NotNull(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    String donorEmail;

    @NotNull(message = "Tên nhân viên không được để trống")
    String staffName;
}
