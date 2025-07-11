package com.example.demo.api;

import com.example.demo.dto.request.CertififcateRequest;
import com.example.demo.dto.response.CertificateResponse;
import com.example.demo.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blood-register")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class CertificateAPI {
    @Autowired
    CertificateService certificateService;

    @PostMapping("/create-certificate")
    @Operation(summary = "Tạo chứng nhận hiến máu")
    public ResponseEntity<CertificateResponse> create(@RequestBody CertififcateRequest request){
        return ResponseEntity.ok(certificateService.create(request));
    }

    @GetMapping("/get-certificate/{id}")
    @Operation(summary = "Lấy chứng nhận hiến máu theo ID")
    public ResponseEntity<CertificateResponse> getCertificateById(@PathVariable Long id) {
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }
}
