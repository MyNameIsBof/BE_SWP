package com.example.demo.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blood-recive")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor

public class BloodReceiveAPI {
}
