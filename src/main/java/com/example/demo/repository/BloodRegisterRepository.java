package com.example.demo.repository;

import com.example.demo.entity.BloodRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodRegisterRepository extends JpaRepository<BloodRegister, Long> {

}
