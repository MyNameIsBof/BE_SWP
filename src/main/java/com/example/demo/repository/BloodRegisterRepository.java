package com.example.demo.repository;

import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.entity.BloodRegister;
import com.example.demo.enums.BloodRegisterStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRegisterRepository extends JpaRepository<BloodRegister, Long> {
    List<BloodRegister> findByStatusIn(List<BloodRegisterStatus> statuses);
    List<BloodRegister> findByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, BloodRegisterStatus status);
}
