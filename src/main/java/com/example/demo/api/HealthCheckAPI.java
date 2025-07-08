package com.example.demo.api;


import com.example.demo.dto.request.HealthCheckRequest;
import com.example.demo.dto.response.HealthCheckResponse;
import com.example.demo.service.HealthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health-check")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class HealthCheckAPI {
    @Autowired
    private final HealthCheckService healthCheckService;

    @PostMapping("/create")
    @Operation(summary = "Tạo mới kiểm tra sức khỏe cho người hiến máu")
    public ResponseEntity<HealthCheckResponse> create(HealthCheckRequest healthCheckRequest) {
        HealthCheckResponse response = healthCheckService.create(healthCheckRequest);
        return ResponseEntity.ok(response);
    }
}
