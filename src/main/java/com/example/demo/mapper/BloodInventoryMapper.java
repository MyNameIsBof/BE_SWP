package com.example.demo.mapper;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.BloodInventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BloodInventoryMapper {
    BloodInventory toBloodInventory(BloodInventoryRequest request);
    BloodInventoryResponse toBloodInventoryResponse(BloodInventory bloodInventory);
}
