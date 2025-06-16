package com.example.demo.service;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.dto.response.BloodInventorySummaryResponse;
import com.example.demo.entity.BloodInventory;
import com.example.demo.enums.BloodType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.BloodInventoryMapper;
import com.example.demo.repository.BloodInventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloodInventoryService {

    private final BloodInventoryRepository repository;
    private final BloodInventoryMapper mapper;

    /**
     * Get all blood inventory records with pagination
     */
    public Page<BloodInventoryResponse> getAllInventory(Pageable pageable) {
        return repository.findByDeletedFalse(pageable)
                .map(mapper::toResponse);
    }

    /**
     * Get blood inventory by ID
     */
    public BloodInventoryResponse getInventoryById(Long id) {
        BloodInventory inventory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood inventory with id " + id + " not found"));

        if (inventory.isDeleted()) {
            throw new ResourceNotFoundException("Blood inventory with id " + id + " has been deleted");
        }

        return mapper.toResponse(inventory);
    }

    /**
     * Create new blood inventory
     */
    @Transactional
    public BloodInventoryResponse createInventory(BloodInventoryRequest request) {
        validateExpirationDate(request.getExpirationDate());

        BloodInventory inventory = mapper.toEntity(request);
        BloodInventory saved = repository.save(inventory);

        return mapper.toResponse(saved);
    }

    /**
     * Update existing blood inventory
     */
    @Transactional
    public BloodInventoryResponse updateInventory(Long id, BloodInventoryRequest request) {
        BloodInventory inventory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood inventory with id " + id + " not found"));

        if (inventory.isDeleted()) {
            throw new ResourceNotFoundException("Blood inventory with id " + id + " has been deleted");
        }

        if (request.getExpirationDate() != null) {
            validateExpirationDate(request.getExpirationDate());
        }

        mapper.updateEntityFromRequest(request, inventory);
        BloodInventory updated = repository.save(inventory);

        return mapper.toResponse(updated);
    }

    /**
     * Soft delete blood inventory
     */
    @Transactional
    public void deleteInventory(Long id) {
        if (!repository.existsByInventoryIdAndDeletedFalse(id)) {
            throw new ResourceNotFoundException("Blood inventory with id " + id + " not found or already deleted");
        }

        repository.softDelete(id);
    }

    /**
     * Get summary of blood inventory by blood type
     */
    public List<BloodInventorySummaryResponse> getInventorySummary() {
        return repository.getTotalUnitsByBloodType();
    }

    /**
     * Find by blood type
     */
    public List<BloodInventoryResponse> findByBloodType(BloodType bloodType) {
        return repository.findByBloodTypeAndDeletedFalse(bloodType)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find expired blood
     */
    public List<BloodInventoryResponse> findExpiredBlood() {
        return repository.findExpiredBlood(new Date())
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find blood expiring soon (next 30 days)
     */
    public List<BloodInventoryResponse> findBloodExpiringSoon() {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date thirtyDaysLater = calendar.getTime();

        return repository.findByExpirationDateRange(today, thirtyDaysLater)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find available blood by type and minimum quantity
     */
    public List<BloodInventoryResponse> findAvailableBloodByType(BloodType bloodType, int minUnits) {
        return repository.findAvailableBloodByType(bloodType, minUnits)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Restore a soft-deleted blood inventory
     */
    @Transactional
    public BloodInventoryResponse restoreInventory(Long id) {
        BloodInventory inventory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blood inventory with id " + id + " not found"));

        if (!inventory.isDeleted()) {
            throw new IllegalStateException("Blood inventory with id " + id + " is not deleted");
        }

        inventory.setDeleted(false);
        BloodInventory restored = repository.save(inventory);

        return mapper.toResponse(restored);
    }

    /**
     * Helper method to validate expiration date
     */
    private void validateExpirationDate(Date expirationDate) {
        Date today = new Date();
        if (expirationDate.before(today)) {
            throw new IllegalArgumentException("Expiration date must be in the future");
        }
    }
}