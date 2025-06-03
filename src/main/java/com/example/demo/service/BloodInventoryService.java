package com.example.demo.service;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.BloodInventory;
import com.example.demo.repository.BloodInventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodInventoryService {
    private final BloodInventoryRepository bloodInventoryRepository;

    public BloodInventoryService(BloodInventoryRepository bloodInventoryRepository) {
        this.bloodInventoryRepository = bloodInventoryRepository;
    }

    // Convert Entity to Response DTO
    private BloodInventoryResponse toResponse(BloodInventory bi) {
        BloodInventoryResponse response = new BloodInventoryResponse();
        response.setInventoryId(bi.getInventoryId());
        response.setInstitutionId(bi.getInstitutionId());
        response.setBloodType(bi.getBloodType());
        response.setUnitsAvailable(bi.getUnitsAvailable());
        response.setAddress(bi.getAddress());
        response.setExpirationDate(bi.getExpirationDate());
        return response;
    }

    // Convert Request DTO to Entity
    private BloodInventory toEntity(BloodInventoryRequest req) {
        return BloodInventory.builder()
                .institutionId(req.getInstitutionId())
                .bloodType(req.getBloodType())
                .unitsAvailable(req.getUnitsAvailable())
                .address(req.getAddress())
                .expirationDate(req.getExpirationDate())
                .build();
    }

    public List<BloodInventoryResponse> getAll() {
        return bloodInventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BloodInventoryResponse getById(Long id) {
        BloodInventory bi = bloodInventoryRepository.findById(id).orElse(null);
        return bi == null ? null : toResponse(bi);
    }

    public BloodInventoryResponse create(BloodInventoryRequest req) {
        BloodInventory saved = bloodInventoryRepository.save(toEntity(req));
        return toResponse(saved);
    }

    public BloodInventoryResponse update(Long id, BloodInventoryRequest req) {
        BloodInventory old = bloodInventoryRepository.findById(id).orElseThrow();
        old.setInstitutionId(req.getInstitutionId());
        old.setBloodType(req.getBloodType());
        old.setUnitsAvailable(req.getUnitsAvailable());
        old.setAddress(req.getAddress());
        old.setExpirationDate(req.getExpirationDate());
        BloodInventory saved = bloodInventoryRepository.save(old);
        return toResponse(saved);
    }

    public void delete(Long id) {
        bloodInventoryRepository.deleteById(id);
    }
}
