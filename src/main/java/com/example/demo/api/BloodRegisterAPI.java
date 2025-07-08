package com.example.demo.api;

import com.example.demo.dto.request.BloodRegisterRequest;
import com.example.demo.dto.request.BloodSetCompletedRequest;
import com.example.demo.dto.response.BloodRegisterListResponse;
import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.entity.BloodRegister;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.service.BloodRegisterService;
import io.swagger.v3.oas.annotations.Operation;
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


    @PostMapping("/create")
    @Operation(summary = "Tạo 1 đơn hiến máu mới")
    public ResponseEntity<BloodRegisterResponse> create(@RequestBody BloodRegisterRequest bloodRegisterRequest) {
        return ResponseEntity.ok(bloodRegisterService.create(bloodRegisterRequest));
    }

    @PutMapping("update/{id}")
    @Operation(summary = "Cập nhật thông tin đơn hiến máu theo ID")
    public ResponseEntity<BloodRegisterResponse> update(@PathVariable Long id, @RequestBody BloodRegisterRequest bloodRegisterRequest) {
        return ResponseEntity.ok(bloodRegisterService.update(id, bloodRegisterRequest));
    }

    @PostMapping("/set-complete")
    @Operation(summary = "Đánh dấu đơn hiến máu là đã hoàn thành")
    public ResponseEntity<BloodRegisterResponse> setCompleted(@RequestBody BloodSetCompletedRequest bloodSetCompletedRequest) {
        return ResponseEntity.ok(bloodRegisterService.setCompleted(bloodSetCompletedRequest));
    }

    @PatchMapping("/update-status/{id}")
    @Operation(summary = "Cập nhật trạng thái của đơn hiến máu theo ID")
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam("status") BloodRegisterStatus status) {
        bloodRegisterService.updateStatus(id, status);
        return ResponseEntity.ok("Status updated successfully");
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lấy danh sách đơn hiến máu theo ID người dùng")
    public ResponseEntity<List<BloodRegisterListResponse>> getByUserId(@PathVariable Long userId) {
        List<BloodRegisterListResponse> result = bloodRegisterService.getByUserId(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list-by-status")
    @Operation(summary = "Lấy danh sách đơn hiến máu theo trạng thái")
    public ResponseEntity<List<BloodRegisterListResponse>> getByStatus(@RequestParam(value = "status", required = false) List<BloodRegisterStatus> statuses) {
        List<BloodRegisterListResponse> result;

        if (statuses != null && !statuses.isEmpty()) {
            result = bloodRegisterService.getByStatuses(statuses);
        } else {
            result = bloodRegisterService.getAll();
        }
        return ResponseEntity.ok(result);
    }



}
