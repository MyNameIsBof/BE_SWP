package com.example.demo.api;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.dto.response.BloodInventorySummaryResponse;
import com.example.demo.enums.BloodType;
import com.example.demo.service.BloodInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blood-inventory")
@RequiredArgsConstructor
@Tag(name = "Blood Inventory", description = "Blood Inventory Management APIs")
public class BloodInventoryAPI {

    private final BloodInventoryService service;

    @GetMapping
    @Operation(summary = "Get all blood inventory records", description = "Returns paginated list of blood inventory")
    public ResponseEntity<Page<BloodInventoryResponse>> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "inventoryId") String sortBy) {

        return ResponseEntity.ok(service.getAllInventory(
                PageRequest.of(page, size, Sort.by(sortBy))));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get blood inventory summary", description = "Returns summary of blood inventory by blood type")
    public ResponseEntity<List<BloodInventorySummaryResponse>> getInventorySummary() {
        return ResponseEntity.ok(service.getInventorySummary());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get blood inventory by ID", description = "Returns a blood inventory by its ID")
    @ApiResponse(responseCode = "200", description = "Found the blood inventory")
    @ApiResponse(responseCode = "404", description = "Blood inventory not found", content = @Content)
    public ResponseEntity<BloodInventoryResponse> getInventoryById(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.getInventoryById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new blood inventory", description = "Creates a new blood inventory record")
    @ApiResponse(responseCode = "201", description = "Blood inventory created")
    public ResponseEntity<BloodInventoryResponse> createInventory(
            @Valid @RequestBody BloodInventoryRequest request) {
        return new ResponseEntity<>(service.createInventory(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update blood inventory", description = "Updates an existing blood inventory record")
    @ApiResponse(responseCode = "200", description = "Blood inventory updated")
    @ApiResponse(responseCode = "404", description = "Blood inventory not found", content = @Content)
    public ResponseEntity<BloodInventoryResponse> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody BloodInventoryRequest request) {
        return ResponseEntity.ok(service.updateInventory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete blood inventory", description = "Soft deletes a blood inventory record")
    @ApiResponse(responseCode = "200", description = "Blood inventory deleted")
    @ApiResponse(responseCode = "404", description = "Blood inventory not found", content = @Content)
    public ResponseEntity<Map<String, String>> deleteInventory(
            @PathVariable Long id) {
        service.deleteInventory(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Blood inventory with id " + id + " has been deleted");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore deleted blood inventory", description = "Restores a soft-deleted blood inventory")
    @ApiResponse(responseCode = "200", description = "Blood inventory restored")
    @ApiResponse(responseCode = "404", description = "Blood inventory not found", content = @Content)
    public ResponseEntity<BloodInventoryResponse> restoreInventory(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.restoreInventory(id));
    }

    @GetMapping("/type/{bloodType}")
    @Operation(summary = "Find by blood type", description = "Returns blood inventory records by blood type")
    public ResponseEntity<List<BloodInventoryResponse>> findByBloodType(
            @PathVariable BloodType bloodType) {
        return ResponseEntity.ok(service.findByBloodType(bloodType));
    }

    @GetMapping("/expired")
    @Operation(summary = "Find expired blood", description = "Returns expired blood inventory records")
    public ResponseEntity<List<BloodInventoryResponse>> findExpiredBlood() {
        return ResponseEntity.ok(service.findExpiredBlood());
    }

    @GetMapping("/expiring-soon")
    @Operation(summary = "Find blood expiring soon", description = "Returns blood inventory records expiring in next 30 days")
    public ResponseEntity<List<BloodInventoryResponse>> findBloodExpiringSoon() {
        return ResponseEntity.ok(service.findBloodExpiringSoon());
    }

    @GetMapping("/available")
    @Operation(summary = "Find available blood", description = "Returns available blood by type and minimum quantity")
    public ResponseEntity<List<BloodInventoryResponse>> findAvailableBloodByType(
            @RequestParam BloodType bloodType,
            @RequestParam(defaultValue = "1") int minUnits) {
        return ResponseEntity.ok(service.findAvailableBloodByType(bloodType, minUnits));
    }
}