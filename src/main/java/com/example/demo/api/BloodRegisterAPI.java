package com.example.demo.api;

import com.example.demo.dto.request.BloodRegisterRequest;
import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.entity.BloodRegister;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.service.BloodRegisterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-register")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class BloodRegisterAPI {
    private final BloodRegisterService bloodRegisterService;

    @GetMapping("/list-all")
    public ResponseEntity<List<BloodRegister>> getAll() {
        return ResponseEntity.ok(bloodRegisterService.getAll());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<BloodRegister> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(bloodRegisterService.getById(id));
//    }

    @PostMapping
    public ResponseEntity<BloodRegisterResponse> create(@RequestBody BloodRegisterRequest bloodRegisterRequest) {
        return ResponseEntity.ok(bloodRegisterService.create(bloodRegisterRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BloodRegisterResponse> update(@PathVariable Long id, @RequestBody BloodRegisterRequest bloodRegisterRequest) {
        return ResponseEntity.ok(bloodRegisterService.update(id, bloodRegisterRequest));
    }

    @PatchMapping("/update-status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam("status") BloodRegisterStatus status) {
        bloodRegisterService.updateStatus(id, status);
        return ResponseEntity.ok("Status updated successfully");
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        bloodRegisterService.delete(id);
//        return ResponseEntity.noContent().build();
//    }

}
