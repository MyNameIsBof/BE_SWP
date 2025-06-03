package com.example.demo.api;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.service.BloodInventoryService;
import jakarta.validation.Valid;
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
    public List<BloodInventoryResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BloodInventoryResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public BloodInventoryResponse create(@Valid @RequestBody BloodInventoryRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public BloodInventoryResponse update(@PathVariable Long id, @RequestBody BloodInventoryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
