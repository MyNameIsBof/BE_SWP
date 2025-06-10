package com.example.demo.entity;

import com.example.demo.enums.BloodType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

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
     Long inventoryId;
     String institutionId;
    @Enumerated(EnumType.STRING)
    BloodType bloodType;
     int unitsAvailable;
     String address;
    @Temporal(TemporalType.DATE)
     Date expirationDate;
    @OneToMany(mappedBy = "bloodInventory",cascade = CascadeType.ALL)
    List<Blood> bloods;

}
