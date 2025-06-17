package com.example.demo.api;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.request.BloodRegisterProcessRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.BloodInventory;
import com.example.demo.repository.BloodInventoryRepository;
import com.example.demo.service.BloodInventoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-inventory")
@SecurityRequirement(name = "api")
public class BloodInventoryAPI {
    public BloodInventoryAPI(BloodInventoryService service) {
        this.service = service;
    }
    private final BloodInventoryService service;

    @Autowired
    BloodInventoryRepository  bloodInventoryRepository;

//    @GetMapping
//    public ResponseEntity<List<BloodInventoryResponse>> getAll() {
//        return ResponseEntity.ok(service.getAll());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<BloodInventoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<BloodInventoryResponse> create(@Valid @RequestBody BloodInventoryRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PostMapping("generate")
    public ResponseEntity<String> generate() {
        service.generateDefaultBloodInventory();
        return ResponseEntity.ok("Blood inventory generated successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloodInventoryResponse> update(@PathVariable Long id, @Valid @RequestBody BloodInventoryRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("update-quantity/{id}")
//    public ResponseEntity<BloodInventoryResponse> updateQuantity(@PathVariable Long id, @Valid @RequestBody BloodRegisterProcessRequest request) {
//        return ResponseEntity.ok(service.updateQuantity(id, request));
//    }
}