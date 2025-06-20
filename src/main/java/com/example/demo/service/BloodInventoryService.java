package com.example.demo.service;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.request.BloodRegisterProcessRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.Blood;
import com.example.demo.entity.BloodInventory;
import com.example.demo.entity.BloodRegister;
import com.example.demo.entity.User;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.mapper.BloodInventoryMapper;
import com.example.demo.repository.BloodInventoryRepository;
import com.example.demo.repository.BloodRegisterRepository;
import com.example.demo.repository.BloodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BloodInventoryService {

    @Autowired
    BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    BloodInventoryMapper bloodInventoryMapper;

    @Autowired
    BloodRepository bloodRepository;

    public List<BloodInventoryResponse> getAll() {
        try {
            List<BloodInventoryResponse> responseList = new ArrayList<>();

            for (BloodType type : BloodType.values()) {
                List<BloodInventory> inventoryList = bloodInventoryRepository.findAllByBloodType(type);
                BloodInventoryResponse response = new BloodInventoryResponse();
                response.setBloodType(type);
                float totalUnit = (float) inventoryList.stream()
                        .mapToDouble(BloodInventory::getUnitsAvailable)
                        .sum();


                response.setUnitsAvailable(totalUnit);
                responseList.add(response);
            }

            return responseList;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving blood inventory list", e);
        }
    }



    public BloodInventoryResponse getById(Long id) {
        try {
            Optional<BloodInventory> optionalBI = bloodInventoryRepository.findById(id);
            if (optionalBI.isEmpty()) {
                throw new RuntimeException("Blood inventory with id " + id + " not found");
            }
            return toResponse(optionalBI.get());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving blood inventory by id: " + id, e);
        }
    }

    public BloodInventoryResponse create(BloodInventoryRequest req) {
        try {
            BloodInventory saved = bloodInventoryRepository.save(toEntity(req));
            return toResponse(saved);
        } catch (Exception e) {
            throw new RuntimeException("Error creating blood inventory", e);
        }
    }

    public BloodInventoryResponse update(Long id, BloodInventoryRequest req) {
        try {
            BloodInventory old = bloodInventoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Blood inventory with id " + id + " not found"));

            // Use the mapper instead of manual field setting
            BloodInventory updated = bloodInventoryMapper.updateBloodInventory(old, req);
            BloodInventory saved = bloodInventoryRepository.save(updated);
            return toResponse(saved);
        } catch (Exception e) {
            throw new RuntimeException("Error updating blood inventory with id: " + id, e);
        }
    }

    public void delete(Long id) {
        try {
            if (!bloodInventoryRepository.existsById(id)) {
                throw new RuntimeException("Blood inventory with id " + id + " not found");
            }
            bloodInventoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting blood inventory with id: " + id, e);
        }
    }

    // Helper methods for conversion between entity and DTOs
    private BloodInventory toEntity(BloodInventoryRequest request) {
        return bloodInventoryMapper.toBloodInventory(request);
    }

    private BloodInventoryResponse toResponse(BloodInventory entity) {
        return bloodInventoryMapper.toBloodInventoryResponse(entity);
    }

    @Scheduled(cron = "0 0 0 * * *") // Chạy mỗi ngày lúc 0h00
    public void resetExpiredBloodUnits() {
        LocalDate today = LocalDate.now();
//        danh sach mau hết hạn
        List<Blood> expiredBloodList = bloodRepository.findByExpirationDateBefore(today);

        for (Blood expiredBlood : expiredBloodList) {
            BloodInventory inventory = expiredBlood.getBloodInventory();
            inventory.setUnitsAvailable(0);
            bloodInventoryRepository.save(inventory);
        }

        bloodInventoryRepository.saveAll(
                expiredBloodList.stream().map(Blood::getBloodInventory).distinct().toList()
        );
    }


}
