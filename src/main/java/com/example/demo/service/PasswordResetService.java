package com.example.demo.service;

import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.OTPResponse;
import com.example.demo.entity.PasswordResetOtp;
import com.example.demo.entity.User;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.mapper.OTPMapper;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.PasswordResetOtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetOtpRepository otpRepository;
    private final AuthenticationRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    OTPMapper otpMapper;

    // OTP expires after 5 minutes
    private static final int OTP_EXPIRY_MINUTES = 5;

    @Transactional
    public void sendOtp(ForgotPasswordRequest request) {
        String email = request.getEmail();

        // Check if email exists
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new GlobalException("Email không tồn tại trong hệ thống");
        }

        // Delete any existing OTP for this email
        otpRepository.deleteByEmail(email);

        // Generate a 6-digit OTP
        String otp = generateOtp();

        // Save the OTP
        PasswordResetOtp passwordResetOtp = PasswordResetOtp.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build();

        otpRepository.save(passwordResetOtp);

        // Send email with OTP
        String subject = "Mã OTP đặt lại mật khẩu";

        emailService.sendResetOTPMail(email, subject, otp, OTP_EXPIRY_MINUTES);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        // Lấy email từ token
        String email;
        try {
            email = tokenService.extractClaim(token, io.jsonwebtoken.Claims::getSubject);
        } catch (Exception e) {
            throw new GlobalException("Token không hợp lệ hoặc đã hết hạn");
        }

        // Lấy user từ email
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new GlobalException("Người dùng không tồn tại");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Gửi email xác nhận
        String subject = "Mật khẩu của bạn đã được đặt lại";
        emailService.sendAcpResetPasswordMail(email, subject);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    @Transactional
    public OTPResponse verifyOtp(String email, String otp) {
        if (otp == null || otp.isBlank()) {
            throw new GlobalException("Mã OTP không được để trống");
        }

        // Tìm OTP
        Optional<PasswordResetOtp> otpOptional = otpRepository.findByEmailAndOtpAndUsedFalse(email, otp);

        if (otpOptional.isEmpty()) {
            throw new GlobalException("Mã OTP không hợp lệ hoặc đã hết hạn");
        }

        PasswordResetOtp passwordResetOtp = otpOptional.get();

        // Kiểm tra xem OTP đã hết hạn chưa
        if (passwordResetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new GlobalException("Mã OTP đã hết hạn");
        }

        // OTP hợp lệ, đánh dấu đã được xác thực và đã sử dụng
        passwordResetOtp.setVerifiedOTP(true);
        passwordResetOtp.setUsed(true); // thường nên đánh dấu luôn là đã sử dụng
        otpRepository.save(passwordResetOtp);

        // Lấy user và sinh token
        User user = authenticationRepository.findUserByEmail(email);
        if (user == null) {
            throw new GlobalException("Không tìm thấy người dùng với email này");
        }
        String token = tokenService.generateToken(user);

        OTPResponse response = otpMapper.toOTPResponse(passwordResetOtp);
        response.setToken(tokenService.generateToken(user));

        return response;
    }
}