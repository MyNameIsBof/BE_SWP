package com.example.demo.service;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.BloodInventory;
import com.example.demo.mapper.BloodInventoryMapper;
import com.example.demo.repository.BloodInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BloodInventoryService {

    @Autowired
    BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    BloodInventoryMapper bloodInventoryMapper;

    public List<BloodInventoryResponse> getAll() {
        try {
            return bloodInventoryRepository.getTotalUnitsByBloodType();
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
}
