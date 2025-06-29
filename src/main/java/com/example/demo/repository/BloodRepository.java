package com.example.demo.repository;

import com.example.demo.entity.Blood;
import com.example.demo.enums.BloodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BloodRepository extends JpaRepository<Blood, Long> {
    List<Blood> findByExpirationDateBefore(LocalDate date);
    Optional<Blood> findByBloodRegisterId(Long bloodRegisterId);

}
