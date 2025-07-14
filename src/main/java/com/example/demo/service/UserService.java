package com.example.demo.service;

import com.example.demo.dto.request.UpdateStatusRequest;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.response.CheckDonationAbilityResponse;
import com.example.demo.dto.response.RemindResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.enums.UserStatus;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthenticationService authenticationService;

    public UserResponse updateUser(UserRequest userRequest){;
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser != null) {
            Optional.ofNullable(userRequest.getFullName()).ifPresent(currentUser::setFullName);
            Optional.ofNullable(userRequest.getPhone()).ifPresent(currentUser::setPhone);
            Optional.ofNullable(userRequest.getAddress()).ifPresent(currentUser::setAddress);
            Optional.ofNullable(userRequest.getGender()).ifPresent(currentUser::setGender);
            Optional.ofNullable(userRequest.getBirthdate()).ifPresent(currentUser::setBirthdate);
            Optional.ofNullable(userRequest.getLastDonation()).ifPresent(currentUser::setLastDonation);
            Optional.ofNullable(userRequest.getHeight()).ifPresent(currentUser::setHeight);
            Optional.ofNullable(userRequest.getWeight()).ifPresent(currentUser::setWeight);
            Optional.ofNullable(userRequest.getMedicalHistory()).ifPresent(currentUser::setMedicalHistory);
            Optional.ofNullable(userRequest.getEmergencyName()).ifPresent(currentUser::setEmergencyName);
            Optional.ofNullable(userRequest.getEmergencyPhone()).ifPresent(currentUser::setEmergencyPhone);
            Optional.ofNullable(userRequest.getBloodType()).ifPresent(currentUser::setBloodType);
        } else{
            throw new AuthenticationException("User not found");
        }


            authenticationRepository.save(currentUser);
            UserResponse userResponse = UserResponse.builder()
                    .fullName(userRequest.getFullName())
                    .phone(userRequest.getPhone())
                    .address(userRequest.getAddress())
                    .gender(userRequest.getGender())
                    .birthdate(userRequest.getBirthdate())
                    .height(userRequest.getHeight())
                    .weight(userRequest.getWeight())
                    .medicalHistory(userRequest.getMedicalHistory())
                    .emergencyName(userRequest.getEmergencyName())
                    .emergencyPhone(userRequest.getEmergencyPhone())
                    .bloodType(userRequest.getBloodType())
                    .build();

            return userResponse;
    }

    public List<UserResponse> getUsersExceptAdmin() {
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser == null || !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AuthenticationException("Bạn không có quyền truy cập vào danh sách người dùng");
        }
       List<User> users = authenticationRepository.findByRoleNot(Role.ADMIN);

        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phone(user.getPhone())
                .address(user.getAddress())
                .gender(user.getGender())
                .birthdate(user.getBirthdate())
                .height(user.getHeight())
                .weight(user.getWeight())
                .lastDonation(user.getLastDonation())
                .medicalHistory(user.getMedicalHistory())
                .emergencyName(user.getEmergencyName())
                .emergencyPhone(user.getEmergencyPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .bloodType(user.getBloodType())
                .build()).toList();
    }
    public CheckDonationAbilityResponse checkHealth(Long id){
        User currentUser = authenticationRepository.getById(id);

        LocalDate birthdate = currentUser.getBirthdate();
        if (birthdate == null) {
            throw new GlobalException("Ngày sinh không được để trống");
        }

        boolean isAdult = 2025 - birthdate.getYear() >= 18;
        boolean enoughWeight = currentUser.getWeight() >= 45;

        LocalDate lastDonation = currentUser.getLastDonation();
        boolean canDonate = (lastDonation == null) || lastDonation.isBefore(LocalDate.now().minusMonths(3));

        if (enoughWeight && isAdult && canDonate) {
            return CheckDonationAbilityResponse.builder()
                    .message("Bạn đã đủ điều kiện hiến máu")
                    .build();
        }

        throw new GlobalException("Chưa đủ điều kiện hiến máu");
    }

    public void updateUserStatus(UpdateStatusRequest request) {
        if (request.getStatus() != UserStatus.ACTIVE) {
            User currentUser = authenticationService.getCurrentUser();
            if (currentUser == null || !currentUser.getRole().equals(Role.ADMIN)) {
                throw new AuthenticationException("Bạn không có quyền cập nhật trạng thái người dùng");
            }
        }
        User userToUpdate = authenticationRepository.findById(request.getUserId())
                .orElseThrow(() -> new GlobalException("Người dùng không tồn tại"));
        userToUpdate.setStatus(request.getStatus());
        authenticationRepository.save(userToUpdate);
    }

    public RemindResponse getDonationReminder() {
        User currentUser = authenticationService.getCurrentUser();
        LocalDate lastDonation = currentUser.getLastDonation();

        if (lastDonation == null) {
            return new RemindResponse("Bạn chưa từng hiến máu. Bạn có thể đăng ký hiến máu bất kỳ lúc nào!");
        }

        LocalDate nextEligibleDate = lastDonation.plusDays(84);
        LocalDate today = LocalDate.now();

        if (!today.isBefore(nextEligibleDate)) {
            return new RemindResponse("Bạn đã đủ điều kiện hiến máu lần tiếp theo. Hãy đăng ký ngay!");
        } else {
            long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, nextEligibleDate);
            return new RemindResponse("Bạn cần chờ thêm " + daysRemaining + " ngày nữa để đủ điều kiện hiến máu tiếp theo.");
        }
    }
}

