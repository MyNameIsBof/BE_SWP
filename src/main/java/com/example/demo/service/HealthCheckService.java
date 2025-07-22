package com.example.demo.service;

import com.example.demo.dto.request.HealthCheckRequest;
import com.example.demo.dto.response.HealthCheckResponse;
import com.example.demo.entity.BloodRegister;
import com.example.demo.entity.HealthCheck;
import com.example.demo.entity.User;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.enums.Gender;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.BloodRegisterRepository;
import com.example.demo.repository.HealthCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HealthCheckService {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    BloodRegisterRepository bloodRegisterRepository;

    @Autowired
    HealthCheckRepository healthCheckRepository;

    @Autowired
    AuthenticationRepository authenticationRepository;

    public HealthCheckResponse create(HealthCheckRequest healthCheckRequest){
        // 1. Lấy nhân viên hiện tại đang thao tác
        User currentUser = authenticationService.getCurrentUser();

        // 2. Tìm đơn hiến máu
        BloodRegister bloodRegister = bloodRegisterRepository.findById(healthCheckRequest.getBloodRegisterId())
                .orElseThrow(() -> new GlobalException("Đơn đăng ký hiến máu không tồn tại"));

        validateHealthCheckInput(healthCheckRequest, bloodRegister.getUser());

        // 3. Lấy user từ đơn đăng ký
        User user = bloodRegister.getUser();

        if (!healthCheckRequest.isStatus() &&
                (healthCheckRequest.getReason() == null || healthCheckRequest.getReason().trim().isEmpty())) {
            throw new GlobalException("Lý do từ chối bắt buộc phải nhập nếu tình trạng sức khỏe không đạt");
        }

        // 4. Tạo bản ghi health check
        HealthCheck healthCheck = HealthCheck.builder()
                .height(healthCheckRequest.getHeight())
                .weight(healthCheckRequest.getWeight())
                .temperature(healthCheckRequest.getTemperature())
                .bloodPressure(healthCheckRequest.getBloodPressure())
                .checkDate(LocalDate.now()) // tự động lấy ngày hiện tại
                .bloodType(bloodRegister.getUser().getBloodType())
                .staffName(currentUser.getFullName())
                .fullName(bloodRegister.getUser().getFullName())
                .medicalHistory(bloodRegister.getUser().getMedicalHistory())
                .status(healthCheckRequest.isStatus())
                .reason(healthCheckRequest.getReason())
                .bloodRegister(bloodRegister)
                .build();

        user.setWeight(healthCheckRequest.getWeight());
        user.setHeight(healthCheckRequest.getHeight());
        authenticationRepository.save(user);

        if (!healthCheckRequest.isStatus()) {
            bloodRegister.setStatus(BloodRegisterStatus.REJECTED);
        }

        healthCheck = healthCheckRepository.save(healthCheck);

        bloodRegister.setHealthCheck(healthCheck);
        bloodRegisterRepository.save(bloodRegister);


        // 5. Trả response có chứa tên người làm đơn và tiền sử bệnh
        return HealthCheckResponse.builder()
                .id(healthCheck.getId())
                .fullName(user.getFullName())
                .medicalHistory(user.getMedicalHistory())
                .height(healthCheck.getHeight())
                .weight(healthCheck.getWeight())
                .temperature(healthCheck.getTemperature())
                .bloodPressure(healthCheck.getBloodPressure())
                .bloodType(bloodRegister.getUser().getBloodType())
                .checkDate(healthCheck.getCheckDate())
                .staffName(healthCheck.getStaffName())
                .status(healthCheck.isStatus())
                .bloodRegisterId(bloodRegister.getId())
                .build();
    }

    public HealthCheckResponse getHealthCheckById(Long id) {
        HealthCheck healthCheck = healthCheckRepository.findById(id)
                .orElseThrow(() -> new GlobalException("Health check not found"));

        return HealthCheckResponse.builder()
                .id(healthCheck.getId())
                .fullName(healthCheck.getFullName())
                .medicalHistory(healthCheck.getMedicalHistory())
                .height(healthCheck.getHeight())
                .weight(healthCheck.getWeight())
                .temperature(healthCheck.getTemperature())
                .bloodPressure(healthCheck.getBloodPressure())
                .checkDate(healthCheck.getCheckDate())
                .bloodType(healthCheck.getBloodType())
                .staffName(healthCheck.getStaffName())
                .status(healthCheck.isStatus())
                .bloodRegisterId(healthCheck.getBloodRegister().getId())
                .build();
    }

    public HealthCheckResponse update(long id, HealthCheckRequest healthCheckRequest) {
        HealthCheck healthCheck = healthCheckRepository.findById(id)
                .orElseThrow(() -> new GlobalException("Health check not found"));

        User currentUser = authenticationService.getCurrentUser();
        if (!currentUser.getRole().equals(Role.STAFF)) {
            throw new GlobalException("Chỉ nhân viên mới có thể cập nhật thông tin kiểm tra sức khỏe");
        }
        BloodRegister bloodRegister = bloodRegisterRepository.findById(healthCheckRequest.getBloodRegisterId())
                .orElseThrow(() -> new GlobalException("Đơn đăng ký hiến máu không tồn tại"));

        validateHealthCheckInput(healthCheckRequest, bloodRegister.getUser());

        // Kiểm tra nếu status là false thì phải có lý do
        if (!healthCheckRequest.isStatus() &&
                (healthCheckRequest.getReason() == null || healthCheckRequest.getReason().trim().isEmpty())) {
            throw new GlobalException("Lý do từ chối bắt buộc phải nhập nếu tình trạng sức khỏe không đạt");
        }

        if (healthCheckRequest.getHeight() != null && healthCheckRequest.getHeight() > 0) {
            healthCheck.setHeight(healthCheckRequest.getHeight());
        }
        if (healthCheckRequest.getWeight() != null && healthCheckRequest.getWeight() > 0) {
            healthCheck.setWeight(healthCheckRequest.getWeight());
        }
        if (healthCheckRequest.getTemperature() != null && healthCheckRequest.getTemperature() > 0) {
            healthCheck.setTemperature(healthCheckRequest.getTemperature());
        }
        if (healthCheckRequest.getBloodPressure() != null && healthCheckRequest.getBloodPressure() > 0) {
            healthCheck.setBloodPressure(healthCheckRequest.getBloodPressure());
        }

        LocalDate checkDate = Optional.ofNullable(healthCheckRequest.getCheckDate()).orElse(LocalDate.now());
        healthCheck.setCheckDate(checkDate);
        Optional.ofNullable(currentUser.getFullName()).ifPresent(healthCheck::setStaffName);
        healthCheck.setStatus(healthCheckRequest.isStatus());
        Optional.ofNullable(healthCheckRequest.getReason()).ifPresent(healthCheck::setReason);


        healthCheck = healthCheckRepository.save(healthCheck);

        return HealthCheckResponse.builder()
                .id(healthCheck.getId())
                .fullName(healthCheck.getFullName())
                .medicalHistory(healthCheck.getMedicalHistory())
                .height(healthCheck.getHeight())
                .weight(healthCheck.getWeight())
                .temperature(healthCheck.getTemperature())
                .bloodPressure(healthCheck.getBloodPressure())
                .checkDate(healthCheck.getCheckDate())
                .staffName(healthCheck.getStaffName())
                .status(healthCheck.isStatus())
                .build();
    }

    public List<HealthCheckResponse> getListHealthChecks() {
        List<HealthCheck> healthChecks = healthCheckRepository.findAll();

        return healthChecks.stream()
                .filter(healthCheck -> healthCheck.getBloodRegister() != null)
                .map(healthCheck -> HealthCheckResponse.builder()
                        .id(healthCheck.getId())
                        .fullName(healthCheck.getFullName())
                        .bloodType(healthCheck.getBloodType())
                        .medicalHistory(healthCheck.getMedicalHistory())
                        .height(healthCheck.getHeight())
                        .weight(healthCheck.getWeight())
                        .temperature(healthCheck.getTemperature())
                        .bloodPressure(healthCheck.getBloodPressure())
                        .checkDate(healthCheck.getCheckDate())
                        .staffName(healthCheck.getStaffName())
                        .status(healthCheck.isStatus())
                        .bloodRegisterId(healthCheck.getBloodRegister().getId())
                        .build())
                .collect(Collectors.toList());
    }

    private void validateHealthCheckInput(HealthCheckRequest request, User user) {
        int age = Period.between(user.getBirthdate(), LocalDate.now()).getYears();
        if (age < 18 || age > 60) {
            throw new GlobalException("Người hiến máu phải từ 18 đến 60 tuổi.");
        }

        if (user.getGender().equals(Gender.MALE) && request.getWeight() < 45) {
            throw new GlobalException("Nam phải ≥ 45kg để hiến máu.");
        }

        if (user.getGender().equals(Gender.FEMALE) && request.getWeight() < 42) {
            throw new GlobalException("Nữ phải ≥ 42kg để hiến máu.");
        }

        if (request.getTemperature() < 35 || request.getTemperature() > 38) {
            throw new GlobalException("Nhiệt độ cơ thể không hợp lệ.");
        }

        if (!isBloodPressureNormal(request.getBloodPressure())) {
            throw new GlobalException("Huyết áp không nằm trong giới hạn cho phép.");
        }

        if (user.getMedicalHistory() != null && containsProhibitedDisease(user.getMedicalHistory())) {
            throw new GlobalException("Tiền sử bệnh không đủ điều kiện hiến máu.");
        }
    }

    private boolean isBloodPressureNormal(Double bloodPressure) {
        if (bloodPressure == null) {
            return false;
        }

        return bloodPressure >= 50 && bloodPressure <= 250;
    }

    private boolean containsProhibitedDisease(String history) {
        String[] prohibited = {"viêm gan", "HIV", "AIDS", "ung thư", "tim mạch"};
        for (String disease : prohibited) {
            if (history.toLowerCase().contains(disease)) {
                return true;
            }
        }
        return false;
    }


}
