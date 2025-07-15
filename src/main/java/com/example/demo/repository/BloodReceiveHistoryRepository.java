package com.example.demo.repository;

import com.example.demo.entity.BloodDonationHistory;
import com.example.demo.entity.BloodReceiveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodReceiveHistoryRepository extends JpaRepository<BloodReceiveHistory, Long> {
}
