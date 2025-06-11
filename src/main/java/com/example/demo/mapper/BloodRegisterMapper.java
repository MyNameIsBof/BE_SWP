package com.example.demo.mapper;


import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.request.BloodRegisterRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.entity.BloodInventory;
import com.example.demo.entity.BloodRegister;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BloodRegisterMapper {

    BloodRegister toBloodRegister(BloodRegisterRequest request);
    BloodRegisterResponse toBloodRegisterResponse(BloodRegister bloodRegister);
}
