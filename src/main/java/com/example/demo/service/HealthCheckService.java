package com.example.demo.service;

import com.example.demo.dto.request.HealthCheckRequest;
import com.example.demo.dto.response.HealthCheckResponse;
import com.example.demo.entity.BloodRegister;
import com.example.demo.entity.HealthCheck;
import com.example.demo.entity.User;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.repository.BloodRegisterRepository;
import com.example.demo.repository.HealthCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class HealthCheckService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    BloodRegisterRepository bloodRegisterRepository;

    @Autowired
    HealthCheckRepository healthCheckRepository;

    public HealthCheckResponse create(HealthCheckRequest healthCheckRequest){
        // 1. Lấy nhân viên hiện tại đang thao tác
        User currentUser = authenticationService.getCurrentUser();

        // 2. Tìm đơn hiến máu
        BloodRegister bloodRegister = bloodRegisterRepository.findById(healthCheckRequest.getBloodRegisterId())
                .orElseThrow(() -> new GlobalException("Đơn đăng ký hiến máu không tồn tại"));

        // 3. Lấy user từ đơn đăng ký
        User user = bloodRegister.getUser();

        // 4. Tạo bản ghi health check
        HealthCheck healthCheck = HealthCheck.builder()
                .height(healthCheckRequest.getHeight())
                .weight(healthCheckRequest.getWeight())
                .temperature(healthCheckRequest.getTemperature())
                .bloodPressure(healthCheckRequest.getBloodPressure())
                .checkDate(LocalDate.now()) // tự động lấy ngày hiện tại
                .staffName(currentUser.getFullName())
                .fullName(bloodRegister.getUser().getFullName())
                .medicalHistory(bloodRegister.getUser().getMedicalHistory())
                .status(healthCheckRequest.getStatus())
                .bloodRegister(bloodRegister)
                .build();

        healthCheck = healthCheckRepository.save(healthCheck);

        // 5. Trả response có chứa tên người làm đơn và tiền sử bệnh
        return HealthCheckResponse.builder()
                .id(healthCheck.getId())
                .fullName(user.getFullName())
                .medicalHistory(user.getMedicalHistory())
                .height(healthCheck.getHeight())
                .weight(healthCheck.getWeight())
                .temperature(healthCheck.getTemperature())
                .bloodPressure(healthCheck.getBloodPressure())
                .checkDate(healthCheck.getCheckDate())
                .staffName(healthCheck.getStaffName())
                .status(healthCheck.getStatus())
                .bloodRegisterId(bloodRegister.getId())
                .build();
    }

}
