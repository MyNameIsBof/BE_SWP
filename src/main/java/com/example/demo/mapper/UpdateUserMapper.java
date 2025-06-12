package com.example.demo.mapper;

import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.UpdateUserResponse;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UpdateUserMapper {
    User toUpdateUser(UpdateUserRequest request);
    UpdateUserResponse toUpdateUserResponse(User user);
}
