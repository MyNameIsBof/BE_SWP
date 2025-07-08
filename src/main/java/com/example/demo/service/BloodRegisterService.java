package com.example.demo.service;

import com.example.demo.dto.request.BloodRegisterProcessRequest;
import com.example.demo.dto.request.BloodRegisterRequest;
import com.example.demo.dto.request.BloodSetCompletedRequest;
import com.example.demo.dto.response.BloodRegisterGetAllResponse;
import com.example.demo.dto.response.BloodRegisterListResponse;
import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.dto.response.HistoryResponse;
import com.example.demo.entity.Blood;
import com.example.demo.entity.BloodInventory;
import com.example.demo.entity.User;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.mapper.BloodRegisterMapper;
import com.example.demo.entity.BloodRegister;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    NotificationService notificationService;

    public List<BloodRegisterListResponse> getAll() {
        List<BloodRegister> bloodRegisters = bloodRegisterRepository.findAll();
        User currentUser = authenticationService.getCurrentUser();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền truy xuất danh sách đơn đăng ký hiến máu");
        }
        return bloodRegisters.stream()
                .map(bloodRegister -> BloodRegisterListResponse.builder()
                        .id(bloodRegister.getId())
                        .wantedDate(bloodRegister.getWantedDate())
                        .wantedHour(bloodRegister.getWantedHour())
                        .status(bloodRegister.getStatus())
                        .bloodType(bloodRegister.getUser().getBloodType())
                        .build())
                .collect(Collectors.toList());
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
        bloodRegister.setUser(currentUser);;
        bloodRegisterRepository.save(bloodRegister);

        // Create notification for blood donation registration
        String donationMessage = "Blood type: " + currentUser.getBloodType() +
                ", Date: " + bloodRegisterRequest.getWantedDate() +
                ", Time: " + bloodRegisterRequest.getWantedHour();
        notificationService.createBloodRequestNotification(currentUser, "Blood donation registration: " + donationMessage);

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
            if (!bloodRegister.getUser().getEmail().equals(currentUser.getEmail())) {
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
                .orElseThrow(() -> new GlobalException("Đơn đăng ký không tồn tại"));
        // check role and status
        switch (status) {
            case APPROVED -> {
                if (authenticationService.getCurrentUser().getRole() != Role.ADMIN) {
                    throw new GlobalException("Bạn không có quyền duyệt đơn đăng ký");
                }
                bloodRegister.setStatus(BloodRegisterStatus.APPROVED);
                bloodRegisterRepository.save(bloodRegister);
                notificationService.createSystemAnnouncementNotification(
                        bloodRegister.getUser(),
                        "Blood Donation Approved",
                        "Your blood donation registration has been approved. Please arrive at the scheduled time."
                );
            }
            case REJECTED -> {
                if (authenticationService.getCurrentUser().getRole() != Role.ADMIN) {
                    throw new GlobalException("Bạn không có quyền từ chối đơn đăng ký");
                }
                bloodRegister.setStatus(BloodRegisterStatus.REJECTED);
                bloodRegisterRepository.save(bloodRegister);
                notificationService.createSystemAnnouncementNotification(
                        bloodRegister.getUser(),
                        "Blood Donation Rejected",
                        "Your blood donation registration has been rejected. Please contact support for more information."
                );
            }
            case INCOMPLETED -> {
                if (authenticationService.getCurrentUser().getRole() != Role.STAFF) {
                    throw new GlobalException("Bạn không có quyền đánh dấu đơn đăng ký chưa hoàn thành");
                }
                bloodRegister.setStatus(BloodRegisterStatus.INCOMPLETED);
                bloodRegisterRepository.save(bloodRegister);
                notificationService.createSystemAnnouncementNotification(
                        bloodRegister.getUser(),
                        "Blood Donation Incomplete",
                        "Your blood donation could not be completed. Please contact the medical staff."
                );
            }
            case CANCELED -> {
                User currentUser = authenticationService.getCurrentUser();
                if (!bloodRegister.getUser().getEmail().equals(currentUser.getEmail()) && currentUser.getRole() != Role.MEMBER) {
                    throw new GlobalException("Bạn không có quyền hủy đơn đăng ký này");
                }
                bloodRegister.setStatus(BloodRegisterStatus.CANCELED);
                bloodRegisterRepository.save(bloodRegister);
                notificationService.createSystemAnnouncementNotification(
                        bloodRegister.getUser(),
                        "Blood Donation Cancelled",
                        "Your blood donation registration has been cancelled."
                );
                break;
            }
            default -> throw new GlobalException("Trạng thái không hợp lệ");
        }

    }


    public List<BloodRegisterListResponse> getByStatuses(List<BloodRegisterStatus> statuses) {
        List<BloodRegister> bloodRegisters = bloodRegisterRepository.findByStatusIn(statuses);

        return bloodRegisters.stream()
                .map(bloodRegister -> BloodRegisterListResponse.builder()
                        .id(bloodRegister.getId())
                        .wantedDate(bloodRegister.getWantedDate())
                        .wantedHour(bloodRegister.getWantedHour())
                        .status(bloodRegister.getStatus())
                        .bloodType(bloodRegister.getUser().getBloodType())
                        .build())
                .collect(Collectors.toList());
    }

    public List<HistoryResponse> getHistoryByUserId(Long userId) {
        List<BloodRegister> bloodRegisters = bloodRegisterRepository.findByUserId(userId);

        if (bloodRegisters.isEmpty()) {
            throw new GlobalException("Không tìm thấy lịch sử đăng ký hiến máu cho người dùng này");
        }

        return bloodRegisters.stream()
                .filter(bloodRegister -> bloodRegister.getStatus() == BloodRegisterStatus.COMPLETED)
                .map(bloodRegister -> {
                    Optional<Blood> bloodOpt = bloodRepository.findByBloodRegisterId(bloodRegister.getId());
                    float unit = bloodOpt.map(Blood::getUnit).orElse(0f);
                    LocalDate completedDate = bloodOpt.map(Blood::getDonationDate).orElse(null);

                    return HistoryResponse.builder()
                            .id(bloodRegister.getUser().getId())
                            .fullName(bloodRegister.getUser().getFullName())
                            .completedDate(completedDate)
                            .unit(unit)
                            .build();
                })
                .collect(Collectors.toList());
    }


    public List<BloodRegisterListResponse> getByUserId(Long userId) {
        List<BloodRegister> bloodRegisters = bloodRegisterRepository.findByUserId(userId);

        if (bloodRegisters.isEmpty()) {
            throw new GlobalException("Không tìm thấy đơn đăng ký nào cho người dùng này");
        }

        return bloodRegisters.stream()
                .map(bloodRegister -> {
                    float unit = 0;
                    if (bloodRegister.getStatus() == BloodRegisterStatus.COMPLETED) {
                        Optional<Blood> blood = bloodRepository.findByBloodRegisterId(bloodRegister.getId());
                        if (blood.isPresent()) {
                            unit = blood.get().getUnit();
                        }
                    }

                    return BloodRegisterListResponse.builder()
                            .id(bloodRegister.getId())
                            .fullName(bloodRegister.getUser().getFullName())
                            .wantedDate(bloodRegister.getWantedDate())
                            .wantedHour(bloodRegister.getWantedHour())
                            .status(bloodRegister.getStatus())
                            .bloodType(bloodRegister.getUser().getBloodType())
                            .unit(unit)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public BloodRegisterResponse setCompleted(BloodSetCompletedRequest bloodSetCompletedRequest) {
        try {
            User currentUser = authenticationService.getCurrentUser();
            if (!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
                throw new GlobalException("Bạn không có quyền thêm máu vào kho máu");
            }
            // 1. Lấy thông tin đơn đăng ký hiến máu
            BloodRegister bloodRegister = bloodRegisterRepository.findById(bloodSetCompletedRequest.getBloodId())
                    .orElseThrow(() -> new GlobalException("Đơn đăng ký không tồn tại"));

            //        2. bo vo kho mau
            BloodInventory bloodInventory = new BloodInventory();
            bloodInventory.setBloodType(bloodRegister.getUser().getBloodType());
            bloodInventory.setUnitsAvailable(bloodSetCompletedRequest.getUnit());
            bloodInventoryRepository.save(bloodInventory);

            // 2. Tạo bản ghi máu mới
            Blood blood = Blood.builder()
                    .bloodType(bloodRegister.getUser().getBloodType())
                    .unit(bloodSetCompletedRequest.getUnit())
                    .expirationDate(bloodSetCompletedRequest.getImplementationDate().plusDays(50))
                    .donationDate(bloodSetCompletedRequest.getImplementationDate())
                    .bloodRegister(bloodRegister)
                    .bloodInventory(bloodInventory)
                    .build();
            bloodRepository.save(blood);

            // 3. Cập nhật trạng thái đơn đăng ký
            bloodRegister.setStatus(BloodRegisterStatus.COMPLETED);
            bloodRegisterRepository.save(bloodRegister);

            // 5. Chuyển sang response trả về
            return BloodRegisterResponse.builder()
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
                    .unit(bloodSetCompletedRequest.getUnit())
                    .build();
        } catch (Exception e) {
            throw new GlobalException("Đơn đã hoàn thành hoặc không tồn tại");
        }
    }

    public List<BloodRegisterGetAllResponse> getListDonation() {
        List<BloodRegister> bloodRegisters = bloodRegisterRepository.findAll();
        User currentUser = authenticationService.getCurrentUser();

        if (!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền truy xuất danh sách đơn đăng ký hiến máu");
        }

        // Map userId -> đơn mới nhất của người đó
        Map<Long, BloodRegister> userLatestRegisterMap = new LinkedHashMap<>();
        for (BloodRegister br : bloodRegisters) {
            long userId = br.getUser().getId();
            // luôn cập nhật để giữ đơn sau cùng (latest entry)
            userLatestRegisterMap.put(userId, br);
        }

        return userLatestRegisterMap.values().stream()
                .map(br -> {
                    User user = br.getUser();
                    int completedCount = getCompletedCountByUser(user.getId());

                    return BloodRegisterGetAllResponse.builder()
                            .id(user.getId()) // ✅ ID người dùng
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .bloodType(user.getBloodType())
                            .lastDonation(user.getLastDonation())
                            .unitDonation(completedCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public int getCompletedCountByUser(Long userId) {
        return (int) bloodRegisterRepository.countByUserIdAndStatus(userId, BloodRegisterStatus.COMPLETED);
    }

}