package com.example.demo.service;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthenticationService authenticationService;


    public UserResponse updateUser(UserRequest userRequest){;
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser != null) {
            Optional.ofNullable(userRequest.getFullName()).ifPresent(currentUser::setFullName);
            Optional.ofNullable(userRequest.getPhone()).ifPresent(currentUser::setPhone);
            Optional.ofNullable(userRequest.getAddress()).ifPresent(currentUser::setAddress);
            Optional.ofNullable(userRequest.getGender()).ifPresent(currentUser::setGender);
            Optional.ofNullable(userRequest.getBirthdate()).ifPresent(currentUser::setBirthdate);
            Optional.ofNullable(userRequest.getHeight()).ifPresent(currentUser::setHeight);
            Optional.ofNullable(userRequest.getWeight()).ifPresent(currentUser::setWeight);
            Optional.ofNullable(userRequest.getMedicalHistory()).ifPresent(currentUser::setMedicalHistory);
            Optional.ofNullable(userRequest.getEmergencyName()).ifPresent(currentUser::setEmergencyName);
            Optional.ofNullable(userRequest.getEmergencyPhone()).ifPresent(currentUser::setEmergencyPhone);
            Optional.ofNullable(userRequest.getBloodType()).ifPresent(currentUser::setBloodType);
        } else{
            throw new AuthenticationException("User not found");
        }


            authenticationRepository.save(currentUser);
            UserResponse userResponse = UserResponse.builder()
                    .fullName(userRequest.getFullName())
                    .phone(userRequest.getPhone())
                    .address(userRequest.getAddress())
                    .gender(userRequest.getGender())
                    .birthdate(userRequest.getBirthdate())
                    .height(userRequest.getHeight())
                    .weight(userRequest.getWeight())
                    .medicalHistory(userRequest.getMedicalHistory())
                    .emergencyName(userRequest.getEmergencyName())
                    .emergencyPhone(userRequest.getEmergencyPhone())
                    .bloodType(userRequest.getBloodType())
                    .build();

            return userResponse;

    }
}

