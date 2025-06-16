package com.example.demo.repository;

import com.example.demo.dto.response.BloodInventorySummaryResponse;
import com.example.demo.entity.BloodInventory;
import com.example.demo.enums.BloodType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {

    @Query("SELECT NEW com.example.demo.dto.response.BloodInventorySummaryResponse(b.bloodType, SUM(b.unitsAvailable)) FROM BloodInventory b WHERE b.deleted = false GROUP BY b.bloodType")
    List<BloodInventorySummaryResponse> getTotalUnitsByBloodType();

    List<BloodInventory> findByBloodTypeAndDeletedFalse(BloodType bloodType);

    @Query("SELECT b FROM BloodInventory b WHERE b.expirationDate <= :date AND b.deleted = false")
    List<BloodInventory> findExpiredBlood(@Param("date") Date date);

    @Query("SELECT b FROM BloodInventory b WHERE b.bloodType = :bloodType AND b.unitsAvailable >= :minUnits AND b.deleted = false")
    List<BloodInventory> findAvailableBloodByType(@Param("bloodType") BloodType bloodType, @Param("minUnits") int minUnits);

    @Query("SELECT b FROM BloodInventory b WHERE b.expirationDate BETWEEN :startDate AND :endDate AND b.deleted = false")
    List<BloodInventory> findByExpirationDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    Page<BloodInventory> findByDeletedFalse(Pageable pageable);

    Page<BloodInventory> findByBloodTypeAndDeletedFalse(BloodType bloodType, Pageable pageable);

    @Modifying
    @Query("UPDATE BloodInventory b SET b.deleted = true WHERE b.inventoryId = :id")
    void softDelete(@Param("id") Long id);

    boolean existsByInventoryIdAndDeletedFalse(Long id);
}