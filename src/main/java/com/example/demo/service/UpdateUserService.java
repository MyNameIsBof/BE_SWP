package com.example.demo.service;

import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.UpdateUserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.mapper.UpdateUserMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.UpdateUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserService {
    @Autowired
    UpdateUserMapper updateUserMapper;

    @Autowired
    UpdateUserRepository updateUserRepository;

    @Autowired
    AuthenticationService authenticationService;


    public UpdateUserResponse updateUser(Long id, UpdateUserRequest updateUserRequest){
        User user = updateUserRepository.findAccountById(id)
                .orElseThrow(() -> new AuthenticationException("Không tồn tại tài khoản này"));;
        User currentUser = authenticationService.getCurrentUser();
        if(user.getRole().equals("ADMIN") || user.getId() == (currentUser.getId())){

            currentUser.setFullName(updateUserRequest.getFullName());
            currentUser.setPhone(updateUserRequest.getPhone());
            currentUser.setAddress(updateUserRequest.getAddress());
            currentUser.setBloodType(updateUserRequest.getBloodType());
            updateUserRepository.save(currentUser);
            UpdateUserResponse updateUserResponse = UpdateUserResponse.builder()
                    .message("Cập nhật thông tin thành công")
                    .success(true)
                    .data(updateUserMapper.toUpdateUserResponse(currentUser))
                    .build();

            return updateUserResponse;
        } else{
                // If the status is not PENDING, throw an exception
                throw new AuthenticationException("Bạn không có quyền cập nhật thông tin này");
        }
    }
}

