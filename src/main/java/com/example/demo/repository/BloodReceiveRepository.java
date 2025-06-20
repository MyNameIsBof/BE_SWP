package com.example.demo.repository;

import com.example.demo.entity.BloodReceive;
import com.example.demo.entity.BloodRegister;
import com.example.demo.enums.BloodReceiveStatus;
import com.example.demo.enums.BloodRegisterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodReceiveRepository extends JpaRepository<BloodReceive, Long> {
    List<BloodReceive> findByStatusIn(List<BloodReceiveStatus> statuses);
}