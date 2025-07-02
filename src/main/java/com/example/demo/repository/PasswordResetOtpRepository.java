package com.example.demo.repository;

import com.example.demo.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findByEmailAndOtpAndUsedFalse(String email, String otp);
    Optional<PasswordResetOtp> findByEmailAndOtpAndVerifiedOTPTrueAndUsedFalse(String email, String otp);
    void deleteByEmail(String email);
}
