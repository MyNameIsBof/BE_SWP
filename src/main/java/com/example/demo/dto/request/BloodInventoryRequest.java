package com.example.demo.dto.request;

import com.example.demo.entity.Blood;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BloodInventoryRequest {
    @NotBlank(message = "Không được để trống")
     String institutionId;
    @NotBlank(message = "Không được để trống")
     String bloodType;
    @Min(1)
     double unitsAvailable;
    @NotBlank(message = "Không được để trống")
     String address;
     Date expirationDate;

     @OneToMany(mappedBy = "blood",cascade = CascadeType.ALL)
    List<Blood> bloodList;

}