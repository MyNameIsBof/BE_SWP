package com.example.demo.api;

import com.example.demo.dto.request.BloodRegisterRequest;
import com.example.demo.dto.request.BloodSetCompletedRequest;
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


    @PostMapping
    public ResponseEntity<BloodRegisterResponse> create(@RequestBody BloodRegisterRequest bloodRegisterRequest) {
        return ResponseEntity.ok(bloodRegisterService.create(bloodRegisterRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BloodRegisterResponse> update(@PathVariable Long id, @RequestBody BloodRegisterRequest bloodRegisterRequest) {
        return ResponseEntity.ok(bloodRegisterService.update(id, bloodRegisterRequest));
    }

    @PutMapping("/set-complete/{id}")
    public ResponseEntity<BloodRegisterResponse> setCompleted(@RequestBody BloodSetCompletedRequest bloodSetCompletedRequest) {
        return ResponseEntity.ok(bloodRegisterService.setCompleted(bloodSetCompletedRequest));
    }

    @PatchMapping("/update-status/{id}")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam("status") BloodRegisterStatus status) {
        bloodRegisterService.updateStatus(id, status);
        return ResponseEntity.ok("Status updated successfully");
    }



    @GetMapping("/list-by-status")
    public ResponseEntity<List<BloodRegister>> getByStatus(@RequestParam(value = "status", required = false) List<BloodRegisterStatus> statuses) {
        List<BloodRegister> result;

        if (statuses != null && !statuses.isEmpty()) {
            result = bloodRegisterService.getByStatuses(statuses);
        } else {
            result = bloodRegisterService.getAll();
        }

        return ResponseEntity.ok(result);
    }



}
