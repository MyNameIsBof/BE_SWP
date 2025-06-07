package com.example.demo.api;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.UpdateUserResponse;
import com.example.demo.entity.User;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationAPI {
    private final AuthenticationService authenticationService;

    @Autowired
    AuthenticationRepository authenticationRespository;

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

    // API endpoint để lấy thông tin người dùng theo email
    @GetMapping("/find-by-email")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        User user = authenticationRespository.findAccountByEmail(email);
        return ResponseEntity.ok(user);
    }

    // API endpoint để cập nhật thông tin người dùng theo email
//    @PutMapping("/update-by-email")
//    public ResponseEntity<UpdateUserResponse> updateUserByEmail(@RequestBody UpdateUserRequest request) {
//        UpdateUserResponse response = authenticationService.updateUserByEmail(request);
//        return ResponseEntity.ok(response);
//    }
}