package com.example.demo.api;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationAPI {
    private final AuthenticationService authenticationService;

    public AuthenticationAPI(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}