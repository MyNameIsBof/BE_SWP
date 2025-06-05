package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "Bloodinventory")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class BloodInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @Column(name = "institution_id")
    private String institutionId;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "units_available")
    private int unitsAvailable; // đổi từ unitAvailable thành unitsAvailable cho chuẩn với DB

    @Column(name = "address")
    private String address;

    @Column(name = "expiration_date")
    @Temporal(TemporalType.DATE)
    private Date expirationDate;
}
