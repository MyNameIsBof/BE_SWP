package com.example.demo.mapper;

import com.example.demo.dto.request.BloodInventoryRequest;
import com.example.demo.dto.response.BloodInventoryResponse;
import com.example.demo.entity.BloodInventory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BloodInventoryMapper {

    BloodInventory toEntity(BloodInventoryRequest request);

    BloodInventoryResponse toResponse(BloodInventory entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(BloodInventoryRequest request, @MappingTarget BloodInventory entity);
}