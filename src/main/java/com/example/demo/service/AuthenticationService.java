package com.example.demo.service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.RegisterResponse;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.BloodTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    AuthenticationRepository authenticationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    BloodTypeRepository bloodTypeRepository;

    public RegisterResponse register(RegisterRequest request){
        try{
            System.out.println(request.getPassword());
            String password = passwordEncoder.encode(request.getPassword());
            User newUser = userMapper.toUser(request);
            newUser.setRole(Role.MEMBER);
            newUser.setStatus(UserStatus.ACTIVE);
            newUser.setPassword(password);
            newUser.setBloodType(request.getBloodType());
            RegisterResponse response = RegisterResponse.builder()
                    .email(newUser.getEmail())
                    .build();
            authenticationRepository.save(newUser);

            return response;
        } catch (Exception e){
            throw new AuthenticationException("Email đã được sử dụng");
        }
    }
    public LoginResponse login(LoginRequest loginRequest){

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("Thong tin dang nhap sai roi");
            throw new AuthenticationException("Sai email hoặc mật khẩu") {
            };
        }

        User user = authenticationRepository.findUserByEmail(loginRequest.email);

        LoginResponse response = userMapper.toUserResponse(user);
        response.setBloodType(user.getBloodType());
        response.setRole(user.getRole());
        response.setToken(tokenService.generateToken(user));

        return response ;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authenticationRepository.findUserByEmail(email);
    }

    public User getCurrentUser() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authenticationRepository.findUserByEmail(email);
    }



}
