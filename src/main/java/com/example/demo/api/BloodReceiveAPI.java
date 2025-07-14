package com.example.demo.api;

import com.example.demo.dto.request.BloodReceiveRequest;
import com.example.demo.dto.request.BloodSetCompletedRequest;
import com.example.demo.dto.response.BloodReceiveResponse;
import com.example.demo.dto.response.BloodReceiveListResponse;
import com.example.demo.enums.BloodReceiveStatus;
import com.example.demo.service.BloodReceiveService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Lấy danh sách yêu cầu nhận máu theo trạng thái")
    public ResponseEntity<List<BloodReceiveListResponse>> getByStatus(@RequestParam(value = "status", required = false) List<BloodReceiveStatus> statuses) {
        List<BloodReceiveListResponse> result;

        if (statuses != null && !statuses.isEmpty()) {
            result = bloodReceiveService.getByStatuses(statuses);
        } else {
            result = bloodReceiveService.getAll();
        }

        return ResponseEntity.ok(result);
    }


    @PostMapping("/create")
    @Operation(summary = "Tạo 1 yêu cầu nhận máu mới")
    public ResponseEntity<BloodReceiveResponse> create(@RequestBody BloodReceiveRequest request) {
        return ResponseEntity.ok(bloodReceiveService.create(request));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Cập nhật thông tin yêu cầu nhận máu theo ID")
    public ResponseEntity<BloodReceiveResponse> update(@PathVariable Long id,
                                                       @RequestBody BloodReceiveRequest request) {
        return ResponseEntity.ok(bloodReceiveService.update(id, request));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Lấy thông tin yêu cầu nhận máu theo ID")
    public ResponseEntity<BloodReceiveResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bloodReceiveService.getByUserId(id));
    }

    @PatchMapping("/update-status/{id}")
    @Operation(summary = "Cập nhật trạng thái của yêu cầu nhận máu theo ID")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestParam BloodReceiveStatus status) {
        bloodReceiveService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set-complete")
    @Operation(summary = "Đánh dấu yêu cầu nhận máu là đã hoàn thành")
    public ResponseEntity<BloodReceiveResponse> setCompleted(@RequestBody BloodSetCompletedRequest bloodSetCompletedRequest) {
        return ResponseEntity.ok(bloodReceiveService.setCompleted(bloodSetCompletedRequest));
    }
}