package com.example.demo.repository;

import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.BloodInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BloodInventoryRepository  extends JpaRepository<BloodInventory, Long>  {
    @Query("SELECT new com.example.demo.dto.response.BloodInventoryResponse(b.bloodType, SUM(b.unitsAvailable)) FROM BloodInventory b GROUP BY b.bloodType")
    List<BloodInventoryResponse> getTotalUnitsByBloodType();
}
