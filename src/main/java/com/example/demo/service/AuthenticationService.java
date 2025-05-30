package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.User;
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

    public User register(User user){
        user.password=passwordEncoder.encode(user.getPassword());
        User newUser = authenticationRepository.save(user);
        return newUser;
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
