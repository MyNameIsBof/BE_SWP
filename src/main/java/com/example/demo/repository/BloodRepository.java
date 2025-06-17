package com.example.demo.repository;

import com.example.demo.entity.Blood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodRepository extends JpaRepository<Blood, Long> {
}
