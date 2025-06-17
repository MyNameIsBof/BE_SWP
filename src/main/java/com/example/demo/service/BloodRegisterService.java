package com.example.demo.service;

import com.example.demo.dto.request.BloodRegisterProcessRequest;
import com.example.demo.dto.request.BloodRegisterRequest;
import com.example.demo.dto.request.BloodSetCompletedRequest;
import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.entity.Blood;
import com.example.demo.entity.BloodInventory;
import com.example.demo.entity.User;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.mapper.BloodRegisterMapper;
import com.example.demo.entity.BloodRegister;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloodRegisterService {
    @Autowired
    BloodRegisterRepository bloodRegisterRepository;

    @Autowired
    BloodRegisterMapper bloodRegisterMapper;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    BloodRepository bloodRepository;
    public List<BloodRegister> getAll() {
        return bloodRegisterRepository.findAll();
    }

    public BloodRegisterResponse create(BloodRegisterRequest bloodRegisterRequest) {
//        User
        User currentUser = authenticationService.getCurrentUser();
        currentUser.setWeight(bloodRegisterRequest.getWeight());
        currentUser.setHeight(bloodRegisterRequest.getHeight());
        currentUser.setBirthdate(bloodRegisterRequest.getBirthdate());
        currentUser.setGender(bloodRegisterRequest.getGender());
        currentUser.setLastDonation(bloodRegisterRequest.getLastDonation());
        currentUser.setMedicalHistory(bloodRegisterRequest.getMedicalHistory());
        currentUser.setEmergencyName(bloodRegisterRequest.getEmergencyName());
        currentUser.setEmergencyPhone(bloodRegisterRequest.getEmergencyPhone());
//        Blood Register Field
        BloodRegister bloodRegister = bloodRegisterMapper.toBloodRegister(bloodRegisterRequest);
        bloodRegister.setStatus(BloodRegisterStatus.PENDING);
        bloodRegister.setUser(currentUser);
        bloodRegisterRepository.save(bloodRegister);

// BloodRegisterResponse
         BloodRegisterResponse bloodRegisterResponse = BloodRegisterResponse.builder()
                 .emergencyName(bloodRegisterRequest.getEmergencyName())
                 .emergencyPhone(bloodRegisterRequest.getEmergencyPhone())
                 .wantedDate(bloodRegisterRequest.getWantedDate())
                 .weight(bloodRegisterRequest.getWeight())
                 .height(bloodRegisterRequest.getHeight())
                 .birthdate(bloodRegisterRequest.getBirthdate())
                 .email(bloodRegister.getUser().getEmail())
                 .fullName(bloodRegister.getUser().getFullName())
                 .phone(bloodRegister.getUser().getPhone())
                 .address(bloodRegister.getUser().getAddress())
                 .gender(bloodRegister.getUser().getGender())
                 .lastDonation(bloodRegister.getUser().getLastDonation())
                 .medicalHistory(bloodRegister.getUser().getMedicalHistory())
                 .bloodType(bloodRegister.getUser().getBloodType())
                 .wantedHour(bloodRegisterRequest.getWantedHour())
                 .build();

        return bloodRegisterResponse;
    }

    @Transactional
    public BloodRegisterResponse update(Long id, BloodRegisterRequest bloodRegisterRequest) {
        // Fetch the existing BloodRegister entity
        BloodRegister bloodRegister = bloodRegisterRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException("Đơn đăng ký không tồn tại"));

        // Check if the status is PENDING (can be updated)
        if (bloodRegister.getStatus() == BloodRegisterStatus.PENDING) {
            // Get the current user from the authentication service
            User currentUser = authenticationService.getCurrentUser();
        if(!bloodRegister.getUser().getEmail().equals(currentUser.getEmail())) {
                throw new AuthenticationException("Bạn không có quyền cập nhật đơn đăng ký này");
            }

            // Update user fields
            currentUser.setWeight(bloodRegisterRequest.getWeight());
            currentUser.setHeight(bloodRegisterRequest.getHeight());
            currentUser.setBirthdate(bloodRegisterRequest.getBirthdate());
            currentUser.setGender(bloodRegisterRequest.getGender());
            currentUser.setLastDonation(bloodRegisterRequest.getLastDonation());
            currentUser.setMedicalHistory(bloodRegisterRequest.getMedicalHistory());
            currentUser.setEmergencyName(bloodRegisterRequest.getEmergencyName());
            currentUser.setEmergencyPhone(bloodRegisterRequest.getEmergencyPhone());


            bloodRegister.setWantedDate(bloodRegisterRequest.getWantedDate());
            bloodRegister.setWantedHour(bloodRegisterRequest.getWantedHour());
            bloodRegister.setStatus(BloodRegisterStatus.PENDING);
            bloodRegister.setUser(currentUser);


            bloodRegisterRepository.save(bloodRegister);

            // Prepare the response with updated fields
            BloodRegisterResponse bloodRegisterResponse = BloodRegisterResponse.builder()
                    .emergencyName(bloodRegisterRequest.getEmergencyName())
                    .emergencyPhone(bloodRegisterRequest.getEmergencyPhone())
                    .wantedDate(bloodRegisterRequest.getWantedDate())
                    .weight(bloodRegisterRequest.getWeight())
                    .height(bloodRegisterRequest.getHeight())
                    .birthdate(bloodRegisterRequest.getBirthdate())
                    .email(bloodRegister.getUser().getEmail())
                    .fullName(bloodRegister.getUser().getFullName())
                    .phone(bloodRegister.getUser().getPhone())
                    .address(bloodRegister.getUser().getAddress())
                    .gender(bloodRegister.getUser().getGender())
                    .lastDonation(bloodRegister.getUser().getLastDonation())
                    .medicalHistory(bloodRegister.getUser().getMedicalHistory())
                    .bloodType(bloodRegister.getUser().getBloodType())
                    .wantedHour(bloodRegisterRequest.getWantedHour())
                    .build();

            return bloodRegisterResponse;
        } else {
            // If the status is not PENDING, throw an exception
            throw new AuthenticationException("Đơn đã được duyệt hoặc không tồn tại");
        }
    }

    public void updateStatus(Long id, BloodRegisterStatus status) {
        // Fetch the existing BloodRegister entity
        BloodRegister bloodRegister = bloodRegisterRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException("Đơn đăng ký không tồn tại"));
        // check role and status
        if(!bloodRegister.getUser().getEmail().equals(authenticationService.getCurrentUser().getEmail())) {
            throw new AuthenticationException("Bạn không có quyền cập nhật đơn đăng ký này");
        }else{
            switch (status){
                case APPROVED -> {
                    if (authenticationService.getCurrentUser().getRole() != Role.ADMIN) {
                        throw new AuthenticationException("Bạn không có quyền duyệt đơn đăng ký");
                    }
                    bloodRegister.setStatus(BloodRegisterStatus.APPROVED);
                    bloodRegisterRepository.save(bloodRegister);
                }
                case REJECTED -> {
                    if (authenticationService.getCurrentUser().getRole() != Role.ADMIN) {
                        throw new AuthenticationException("Bạn không có quyền từ chối đơn đăng ký");
                    }
                    bloodRegister.setStatus(BloodRegisterStatus.REJECTED);
                    bloodRegisterRepository.save(bloodRegister);
                }
                case COMPLETED -> {
                    if (authenticationService.getCurrentUser().getRole() != Role.STAFF) {
                        throw new AuthenticationException("Bạn không có quyền đánh dấu đơn đăng ký đã hoàn thành");
                    }
                    bloodRegister.setStatus(BloodRegisterStatus.COMPLETED);
                    bloodRegisterRepository.save(bloodRegister);
                }
                case INCOMPLETED -> {
                    if (authenticationService.getCurrentUser().getRole() != Role.STAFF) {
                        throw new AuthenticationException("Bạn không có quyền đánh dấu đơn đăng ký chưa hoàn thành");
                    }
                    bloodRegister.setStatus(BloodRegisterStatus.INCOMPLETED);
                    bloodRegisterRepository.save(bloodRegister);
                }
                case CANCELED -> {
                    User currentUser = authenticationService.getCurrentUser();
                    if (!bloodRegister.getUser().getEmail().equals(currentUser.getEmail()) && currentUser.getRole() != Role.MEMBER) {
                        throw new AuthenticationException("Bạn không có quyền hủy đơn đăng ký này");
                    }
                    bloodRegister.setStatus(BloodRegisterStatus.CANCELED);
                    bloodRegisterRepository.save(bloodRegister);
                    break;
                }
                default -> throw new AuthenticationException("Trạng thái không hợp lệ");
            }
        }
    }


    public List<BloodRegister> getByStatuses(List<BloodRegisterStatus> statuses) {
        return bloodRegisterRepository.findByStatusIn(statuses);
    }
    public BloodRegisterResponse setCompleted(BloodSetCompletedRequest bloodSetCompletedRequest) {
        BloodRegister bloodRegister = bloodRegisterRepository.findById(bloodSetCompletedRequest.getBloodRegisterId())
                .orElseThrow(() -> new AuthenticationException("Đơn đăng ký không tồn tại"));
//      add blood to inventory
        BloodInventory bloodInventory = bloodInventoryRepository.findByBloodType(bloodRegister.getUser().getBloodType());
        bloodInventory.setUnitsAvailable(bloodInventory.getUnitsAvailable() + bloodSetCompletedRequest.getUnit() );
//        new blood
        Blood blood = Blood.builder()
                .bloodType(bloodRegister.getUser().getBloodType())
                .unit(bloodSetCompletedRequest.getUnit()) // Assuming a default quantity of 1 unit
                .expirationDate(bloodSetCompletedRequest.getImplementationDate().plusDays(50))
                .donationDate(bloodSetCompletedRequest.getImplementationDate())
                .bloodInventory(bloodInventory)
                .build();
        bloodRepository.save(blood);
//      update status of blood register
        bloodRegister.setStatus(BloodRegisterStatus.COMPLETED);
        bloodRegisterRepository.save(bloodRegister);
//       transform to response
        BloodRegisterResponse bloodRegisterResponse = BloodRegisterResponse.builder()
                .emergencyName(bloodRegister.getUser().getEmergencyName())
                .emergencyPhone(bloodRegister.getUser().getEmergencyPhone())
                .wantedDate(bloodRegister.getWantedDate())
                .weight(bloodRegister.getUser().getWeight())
                .height(bloodRegister.getUser().getHeight())
                .birthdate(bloodRegister.getUser().getBirthdate())
                .email(bloodRegister.getUser().getEmail())
                .fullName(bloodRegister.getUser().getFullName())
                .phone(bloodRegister.getUser().getPhone())
                .address(bloodRegister.getUser().getAddress())
                .gender(bloodRegister.getUser().getGender())
                .lastDonation(bloodRegister.getUser().getLastDonation())
                .medicalHistory(bloodRegister.getUser().getMedicalHistory())
                .bloodType(bloodRegister.getUser().getBloodType())
                .wantedHour(bloodRegister.getWantedHour())
                .build();
        return bloodRegisterResponse;
    }
}
