package com.example.demo.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    public String email;
    public String password;
}
