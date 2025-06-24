package com.example.demo.service;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.Blood;
import com.example.demo.entity.BloodInventory;
import com.example.demo.entity.User;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.exception.exceptions.ResourceNotFoundException;
import com.example.demo.mapper.BloodInventoryMapper;
import com.example.demo.repository.BloodInventoryRepository;
import com.example.demo.repository.BloodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BloodInventoryService {
    @Autowired
    BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    BloodInventoryMapper bloodInventoryMapper;

    @Autowired
    BloodRepository bloodRepository;

    @Autowired
    AuthenticationService authenticationService;

    public List<BloodInventoryResponse> getAll() {
        User currentUser = authenticationService.getCurrentUser();
        if (!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền truy xuất danh sách kho máu");
        }
        try {
            List<BloodInventoryResponse> responseList = new ArrayList<>();

            for (BloodType type : BloodType.values()) {
                List<BloodInventory> inventoryList = bloodInventoryRepository.findAllByBloodType(type);
                BloodInventoryResponse response = new BloodInventoryResponse();
                response.setBloodType(type);
                float totalUnit = (float) inventoryList.stream()//
                        .mapToDouble(BloodInventory::getUnitsAvailable)
                        .sum();

                response.setUnitsAvailable(totalUnit);
                responseList.add(response);
            }

            return responseList;
        } catch (Exception e) {
            throw new GlobalException("Lỗi khi truy xuất danh sách kho máu");
        }
    }

    public BloodInventoryResponse getById(Long id) {
        User currentUser = authenticationService.getCurrentUser();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền truy xuất kho máu");
        }
        //lấy tất cả thông tin theo id
        try {
            BloodInventory inventory = bloodInventoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho máu với ID: " + id));
            return toResponse(inventory);
        } catch (ResourceNotFoundException e) {
            // Let ResourceNotFoundException propagate up without wrapping
            throw e;
        } catch (Exception e) {
            throw new GlobalException("Lỗi khi truy xuất kho máu với ID: " + id);
        }
    }

    public BloodInventoryResponse create(BloodInventoryRequest req) {
        User currentUser = authenticationService.getCurrentUser();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền tạo kho máu");
        }
        try {
            BloodInventory saved = bloodInventoryRepository.save(toEntity(req));
            return toResponse(saved);
        } catch (Exception e) {
            // Use GlobalException consistently
            throw new GlobalException("Lỗi khi tạo mới kho máu");
        }
    }

    public BloodInventoryResponse update(Long id, BloodInventoryRequest req) {
        User currentUser = authenticationService.getCurrentUser();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền cập nhật kho máu");
        }
        try {
            BloodInventory old = bloodInventoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kho máu với ID: " + id));

            // Use the mapper instead of manual field setting
            BloodInventory updated = bloodInventoryMapper.updateBloodInventory(old, req);
            BloodInventory saved = bloodInventoryRepository.save(updated);
            return toResponse(saved);
        } catch (ResourceNotFoundException e) {
            // Let ResourceNotFoundException propagate up without wrapping
            throw e;
        } catch (Exception e) {
            throw new GlobalException("Lỗi khi cập nhật kho máu với ID: " + id);
        }
    }

    public void delete(Long id) {
        User currentUser = authenticationService.getCurrentUser();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền xóa kho máu");
        }
        try {
            if (!bloodInventoryRepository.existsById(id)) {
                // Use ResourceNotFoundException consistently
                throw new ResourceNotFoundException("Không tìm thấy kho máu với ID: " + id);
            }
            bloodInventoryRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            // Let ResourceNotFoundException propagate up without wrapping
            throw e;
        } catch (Exception e) {
            throw new GlobalException("Lỗi khi xóa kho máu với ID: " + id);
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
        try {
            LocalDate today = LocalDate.now();
            // Danh sách máu hết hạn
            List<Blood> expiredBloodList = bloodRepository.findByExpirationDateBefore(today);

            for (Blood expiredBlood : expiredBloodList) {
                BloodInventory inventory = expiredBlood.getBloodInventory();
                inventory.setUnitsAvailable(0);
                bloodInventoryRepository.save(inventory);
            }

            bloodInventoryRepository.saveAll(
                    expiredBloodList.stream().map(Blood::getBloodInventory).distinct().toList()
            );
        } catch (Exception e) {
            throw new GlobalException("Lỗi khi cập nhật máu hết hạn");
        }
    }
}
