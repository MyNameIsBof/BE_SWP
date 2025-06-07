package com.example.demo.api;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.service.BloodInventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-inventory")
public class BloodInventoryAPI {
    private final BloodInventoryService service;

    public BloodInventoryAPI(BloodInventoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BloodInventoryResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<BloodInventoryResponse> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(service.getById(id));
//    }
//
//    @PostMapping
//    public ResponseEntity<BloodInventoryResponse> create(@Valid @RequestBody BloodInventoryRequest request) {
//        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<BloodInventoryResponse> update(@PathVariable Long id, @Valid @RequestBody BloodInventoryRequest request) {
//        return ResponseEntity.ok(service.update(id, request));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        service.delete(id);
//        return ResponseEntity.noContent().build();
//    }
}