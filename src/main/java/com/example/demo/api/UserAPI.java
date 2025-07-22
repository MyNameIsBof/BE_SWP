package com.example.demo.api;



import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;

import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.request.UserRequest;

import com.example.demo.entity.User;
import com.example.demo.enums.UserStatus;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class UserAPI {

    @Autowired
    private final AuthenticationRepository authenticationRepository;

    @Autowired
    private final PasswordResetService passwordResetService;

    @Autowired
    private final UserService updateUserService;

    @PutMapping("update-user")
    @Operation(summary = "Cập nhật thông tin người dùng")
    public ResponseEntity<UpdateUserResponse> update(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(updateUserService.updateUser(userRequest));
    }

    @GetMapping("get-user-by-role")
    @Operation(summary = "Lấy danh sách người dùng trừ admin")
    public ResponseEntity<?> getListUserByRole() {
        return ResponseEntity.ok(updateUserService.getUsersExceptAdmin());
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Gửi mã OTP để đặt lại mật khẩu")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendOtp(request);
        return ResponseEntity.ok("Mã OTP đã được gửi đến email của bạn");
    }

    @GetMapping("/verify-otp")
    @Operation(summary = "Xác thực mã OTP")
    public ResponseEntity<OTPResponse> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        OTPResponse response = passwordResetService.verifyOtp(email, otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Đặt lại mật khẩu bằng mã OTP")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công");
    }


    @PutMapping("/update-status")
    @Operation(summary = "Cập nhật trạng thái người dùng (chỉ Admin)")
    public ResponseEntity<String> updateUserStatus(@Valid @RequestBody UpdateStatusRequest request) {
        updateUserService.updateUserStatus(request);
        return ResponseEntity.ok("Cập nhật trạng thái người dùng thành công");
    }

    @GetMapping("/get-remind")
    @Operation(summary = "Lấy thông báo nhắc nhở")
    public ResponseEntity<RemindResponse> getRemind() {
        return ResponseEntity.ok(updateUserService.getDonationReminder());
    }

    @GetMapping("/check-donation-ability")
    @Operation(summary = "")
    public ResponseEntity<CheckDonationAbilityResponse> checkHealth(@RequestParam Long id) {
        return ResponseEntity.ok(updateUserService.checkHealth(id));
    }


}
