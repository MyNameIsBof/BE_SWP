package com.example.demo.api;


import com.example.demo.dto.request.EmailPasswordRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.EmailPasswordResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class UserAPI {

    private final UserService updateUserService;
    @PutMapping("update-user")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(updateUserService.updateUser(userRequest));
    }

    @PutMapping("/update/email-password")
    public ResponseEntity<EmailPasswordResponse> updateEmailPassword(@RequestBody EmailPasswordRequest emailPasswordRequest) {
        return ResponseEntity.ok(updateUserService.updateEmailPassword(emailPasswordRequest));
    }

    @GetMapping("get-user-by-role")
    public ResponseEntity<?> getListUserByRole() {
        return ResponseEntity.ok(updateUserService.getUsersExceptAdmin());
    }

}
