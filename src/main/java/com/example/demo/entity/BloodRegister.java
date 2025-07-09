package com.example.demo.entity;

import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.enums.HealthCheckStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class BloodRegister {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Enumerated(EnumType.STRING)
    BloodRegisterStatus status;

    LocalDate wantedDate;

    @JsonFormat(pattern = "HH:mm:ss")
    LocalTime wantedHour;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @OneToOne(mappedBy = "bloodRegister",cascade = CascadeType.ALL)
    Blood blood;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "blood_register_id")
    HealthCheck healthCheck;

}
