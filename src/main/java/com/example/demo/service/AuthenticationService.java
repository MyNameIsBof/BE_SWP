package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.model.request.RegisterRequest;
import com.example.demo.model.response.RegisterResponse;
import com.example.demo.repository.AuthenticationRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
                .full_name(request.getFullName())
                .email(request.getEmail())
                .password(password)
                .phone(Long.parseLong(request.getPhoneNumber()))
                .address(request.getAddress())
                .location(request.getLocation())
                .blood_type(request.getBloodType())
                .last_donation(request.getLastDonationDate())
                .role(Role.MEMBER) // Assuming default role is USER
                .build();
        RegisterResponse response = RegisterResponse.builder()
                .fullName(newUser.getFull_name())
                .email(newUser.getEmail())
                .phoneNumber(String.valueOf(newUser.getPhone()))
                .address(newUser.getAddress())
                .location(newUser.getLocation())
                .bloodType(newUser.getBlood_type())
                .lastDonationDate(newUser.getLast_donation())
                .role(newUser.getRole().name())
                .build();
            authenticationRepository.save(newUser);

        return response;
    }
    public User login(LoginRequest loginRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
        }catch (Exception e){
            System.out.println("Thong tin dang nhap sai roi");
            throw new AuthenticationException("Invalid username or password") {
            };
        }
        return authenticationRepository.findAccountByEmail(loginRequest.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authenticationRepository.findAccountByEmail(email);
    }
}
