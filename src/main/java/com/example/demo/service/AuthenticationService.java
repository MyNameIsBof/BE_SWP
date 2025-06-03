package com.example.demo.service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.RegisterResponse;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.repository.AuthenticationRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    AuthenticationManager authenticationManager;//giup check dang nhap

    @Autowired
    AuthenticationRespository authenticationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request){
        String password = passwordEncoder.encode(request.getPassword());
        User newUser = User.builder()
                .full_name(request.getFull_name())
                .email(request.getEmail())
                .password(password)
                .phone(request.getPhone())
                .address(request.getAddress())
                .location(null)
                .blood_type(request.getBlood_type())
                .last_donation(null)
                .role(Role.MEMBER) // Assuming default role is USER
                .build();
        RegisterResponse response = RegisterResponse.builder()
                .full_name(newUser.getFull_name())
                .email(newUser.getEmail())
                .phone(newUser.getPhone())
                .address(newUser.getAddress())
                .location(newUser.getLocation())
                .blood_type(newUser.getBlood_type())
                .last_donation(newUser.getLast_donation())
                .role(newUser.getRole())
                .token(null)
                .build();
            authenticationRepository.save(newUser);

        return response;
    }
    public LoginResponse login(LoginRequest loginRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
        }catch (Exception e){
            System.out.println("Thong tin dang nhap sai roi");
            throw new AuthenticationException("Sai email hoặc mật khẩu") {
            };
        }
        User user = authenticationRepository.findAccountByEmail(loginRequest.getEmail());
        LoginResponse response = LoginResponse.builder()
                .address(user.getAddress())
                .location(user.getLocation())
                .phone(user.getPhone())
                .role(user.getRole())
                .full_name(user.getFull_name())
                .email(user.getEmail())
                .blood_type(user.getBlood_type())
                .last_donation(user.getLast_donation())
                .build();
        return response ;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authenticationRepository.findAccountByEmail(email);
    }
}
