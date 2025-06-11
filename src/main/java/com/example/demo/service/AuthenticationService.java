package com.example.demo.service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.UpdateUserResponse;
import com.example.demo.entity.Blood;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.RegisterResponse;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.BloodTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        System.out.println(request.getPassword());
        String password = passwordEncoder.encode(request.getPassword());
        User newUser = userMapper.toUser(request);
        newUser.setRole(Role.MEMBER);
        newUser.setPassword(password);
        newUser.setBloodType(request.getBloodType());
        RegisterResponse response = RegisterResponse.builder()
                .email(newUser.getEmail())
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
            System.out.println(e.getMessage());
            System.out.println("Thong tin dang nhap sai roi");
            throw new AuthenticationException("Sai email hoặc mật khẩu") {
            };
        }

        User user = authenticationRepository.findAccountByEmail(loginRequest.email);

        LoginResponse response = userMapper.toUserResponse(user);
        response.setBloodType(user.getBloodType());
        response.setRole(user.getRole());
        response.setToken(tokenService.generateToken(user));

        return response ;
    }

//    public UpdateUserResponse updateUserByEmail(UpdateUserRequest request){
//        User exist = authenticationRepository.findAccountByEmail(request.getEmail());
//
//        if(request.getFull_name() != null || !request.getFull_name().isEmpty()){
//            exist.setFull_name(request.getFull_name());
//        }
//        if(request.getPhone() != null || !request.getPhone().isEmpty()){
//            exist.setPhone(request.getPhone());
//        }
//        if(request.getAddress() != null || !request.getAddress().isEmpty()){
//            exist.setAddress(request.getAddress());
//        }
//
//        User updateUser = authenticationRepository.save(exist);
//
//        return UpdateUserResponse.builder().success(true).message("User information updated successfully").data(updateUser).build();
//    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authenticationRepository.findAccountByEmail(email);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authenticationRepository.findAccountByEmail(email);
    }



}
