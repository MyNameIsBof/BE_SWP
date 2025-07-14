package com.example.demo.repository;

import com.example.demo.entity.BloodDonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BloodRepository extends JpaRepository<BloodDonationHistory, Long> {
    List<BloodDonationHistory> findByExpirationDateBefore(LocalDate date);
    Optional<BloodDonationHistory> findByBloodRegisterId(Long bloodRegisterId);

}
