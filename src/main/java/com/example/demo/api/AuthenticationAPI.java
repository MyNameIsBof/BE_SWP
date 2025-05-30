package com.example.demo.api;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationAPI {
    @Autowired
    AuthenticationService authenticationService;
    @PostMapping("/api/register")
    public ResponseEntity register(@RequestBody User user){
        User newAccont = authenticationService.register(user);
        return ResponseEntity.ok(newAccont);
    }
    @PostMapping("/api/login")
    public ResponseEntity register(@RequestBody LoginRequest loginRequest){
        User newUser = authenticationService.login(loginRequest);
        return ResponseEntity.ok(null);
    }
}
