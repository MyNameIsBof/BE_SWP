package com.example.demo.api;

import com.example.demo.dto.request.BloodReceiveRequest;
import com.example.demo.dto.request.BloodSetCompletedRequest;
import com.example.demo.dto.response.BloodReceiveResponse;
import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.entity.BloodReceive;
import com.example.demo.entity.BloodRegister;
import com.example.demo.enums.BloodReceiveStatus;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.service.BloodReceiveService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-receive")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class BloodReceiveAPI {
    // Fix: inject the service instead of the API itself
    private final BloodReceiveService bloodReceiveService;

    @GetMapping("/list-by-status")
    public ResponseEntity<List<BloodReceive>> getByStatus(@RequestParam(value = "status", required = false) List<BloodReceiveStatus> statuses) {
        List<BloodReceive> result;

        if (statuses != null && !statuses.isEmpty()) {
            result = bloodReceiveService.getByStatuses(statuses);
        } else {
            result = bloodReceiveService.getAll();
        }

        return ResponseEntity.ok(result);
    }


    @PostMapping
    public ResponseEntity<BloodReceiveResponse> create(@RequestBody BloodReceiveRequest request) {
        return ResponseEntity.ok(bloodReceiveService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloodReceiveResponse> update(@PathVariable Long id,
                                                       @RequestBody BloodReceiveRequest request) {
        return ResponseEntity.ok(bloodReceiveService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloodReceiveResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bloodReceiveService.getById(id));
    }

    @PatchMapping("/update-status/{id}")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestParam BloodReceiveStatus status) {
        bloodReceiveService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set-complete")
    public ResponseEntity<BloodReceiveResponse> setCompleted(@RequestBody BloodSetCompletedRequest bloodSetCompletedRequest) {
        return ResponseEntity.ok(bloodReceiveService.setCompleted(bloodSetCompletedRequest));
    }
}