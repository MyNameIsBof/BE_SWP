package com.example.demo.entity;

import com.example.demo.enums.BloodType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class BloodInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long inventoryId;
    @Enumerated(EnumType.STRING)// Chỉ định kiểu enum sẽ được lưu dưới dạng chuỗi
    BloodType bloodType;
    @Column(name = "units_available")
    float unitsAvailable;
    @OneToOne(mappedBy = "bloodInventory")
    Blood bloods;
}
