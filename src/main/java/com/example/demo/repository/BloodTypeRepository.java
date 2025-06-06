package com.example.demo.repository;

import com.example.demo.entity.Blood;
import com.example.demo.enums.BloodType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodTypeRepository extends JpaRepository<Blood, Long> {
    Blood findBloodByBloodType(BloodType bloodType);
}
