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
//
//    @Autowired
//    BloodRegisterRepository bloodRegisterRepository;
//
    @Autowired
    AuthenticationService authenticationService;

//    public List<BloodInventoryResponse> getAll() {
//        try {
//            return bloodInventoryRepository.getTotalUnitsByBloodType();
//        } catch (Exception e) {
//            throw new RuntimeException("Error retrieving blood inventory list", e);
//        }
//    }

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


//    public BloodInventoryResponse updateQuantity(Long id, BloodRegisterProcessRequest bloodRegisterProcessRequest){
//        User currentUser = authenticationService.getCurrentUser();
//
//        if (!currentUser.getRole().equals(Role.STAFF)) {
//            throw new AuthenticationException("Bạn không có quyền thực hiện thao tác này");
//        }
//
//        List<BloodInventory> inventories = bloodInventoryRepository.findByBloodType(bloodRegisterProcessRequest.getBloodType());
//        if (inventories.isEmpty()) {
//            throw new RuntimeException("Blood type " + bloodRegisterProcessRequest.getBloodType() + " not found");
//        }
//
//        BloodInventory bloodInventory = inventories.get(0);
//        bloodInventory.setUnitsAvailable(bloodInventory.getUnitsAvailable() + bloodRegisterProcessRequest.getQuantity());
//        bloodInventory.setDonationDate(bloodRegisterProcessRequest.getDonationDate());
//        bloodInventoryRepository.save(bloodInventory);
//        BloodRegister bloodRegister = bloodRegisterRepository.findById(id)
//                .orElseThrow(() -> new AuthenticationException("Đơn đăng ký không tồn tại"));
//        bloodRegister.setStatus(BloodRegisterStatus.COMPLETED);
//        bloodRegisterRepository.save(bloodRegister);
//        return toResponse(bloodInventory);
//
//    }

    public void generateDefaultBloodInventory() {
        User currentUser = authenticationService.getCurrentUser();
        if(Role.STAFF.equals(currentUser.getRole()) || Role.ADMIN.equals(currentUser.getRole())) {
            for (BloodType type : BloodType.values()) {
                boolean exists = bloodInventoryRepository.existsByBloodType(type);
                if (!exists) {
                    BloodInventory inventory = BloodInventory.builder()
                            .bloodType(type)
                            .unitsAvailable(0)
                            .build();
                    bloodInventoryRepository.save(inventory);
                }
            }
        }
        else {
            throw new AuthenticationException("Bạn không có quyền thực hiện thao tác này");
        }
    }
}
