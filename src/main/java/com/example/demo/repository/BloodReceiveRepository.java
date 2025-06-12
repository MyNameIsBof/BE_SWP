package com.example.demo.repository;

import com.example.demo.entity.BloodReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodReceiveRepository extends JpaRepository<BloodReceive, Long> {
}