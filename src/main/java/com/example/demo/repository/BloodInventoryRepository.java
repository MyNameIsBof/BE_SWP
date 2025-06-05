package com.example.demo.repository;

import com.example.demo.entity.BloodInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodInventoryRepository  extends JpaRepository<BloodInventory, Long>  {
}
