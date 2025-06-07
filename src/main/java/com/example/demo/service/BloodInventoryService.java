package com.example.demo.service;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.BloodInventory;
import com.example.demo.mapper.BloodInventoryMapper;
import com.example.demo.repository.BloodInventoryRepository;
import com.example.demo.repository.BloodTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BloodInventoryService {
    @Autowired
    BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    BloodInventoryMapper bloodInventoryMapper;



    public List<BloodInventoryResponse> getAll() {
        return bloodInventoryRepository.getTotalUnitsByBloodType();
    }

//    public BloodInventoryResponse getById(Long id) {
//        BloodInventory bi = bloodInventoryRepository.findById(id).orElse(null);
//        return bi == null ? null : toResponse(bi);
//    }
//
//    public BloodInventoryResponse create(BloodInventoryRequest req) {
//        BloodInventory saved = bloodInventoryRepository.save(toEntity(req));
//        return toResponse(saved);
//    }
//
//    public BloodInventoryResponse update(Long id, BloodInventoryRequest req) {
//        BloodInventory old = bloodInventoryRepository.findById(id).orElseThrow();
//        old.setInstitutionId(req.getInstitutionId());
//        old.setBloodType(req.getBloodType());
//        old.setUnitsAvailable(req.getUnitsAvailable());
//        old.setAddress(req.getAddress());
//        old.setExpirationDate(req.getExpirationDate());
//        BloodInventory saved = bloodInventoryRepository.save(old);
//        return toResponse(saved);
//    }
//
//    public void delete(Long id) {
//        bloodInventoryRepository.deleteById(id);
//    }
}
