package com.example.demo.api;


import com.example.demo.dto.request.BloodRegisterRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.dto.response.UpdateUserResponse;
import com.example.demo.service.UpdateUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/update-user")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class UpdateUserAPI {

    private final UpdateUserService updateUserService;

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserResponse> update(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(updateUserService.updateUser(id, updateUserRequest));
    }
}
