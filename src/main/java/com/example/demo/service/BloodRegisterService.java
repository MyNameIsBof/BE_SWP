package com.example.demo.service;

import com.example.demo.dto.request.BloodRegisterRequest;
import com.example.demo.dto.request.BloodSetCompletedRequest;
import com.example.demo.dto.response.BloodRegisterHistoryResponse;
import com.example.demo.dto.response.BloodRegisterListResponse;
import com.example.demo.dto.response.BloodRegisterResponse;
import com.example.demo.dto.response.DonationHistoryResponse;
import com.example.demo.entity.*;
import com.example.demo.enums.*;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.mapper.BloodRegisterMapper;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
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
    BloodDonationHistoryRepository bloodDonationHistoryRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;



    public List<BloodRegisterListResponse> getAll() {
        User currentUser = authenticationService.getCurrentUser();
        List<BloodRegister> bloodRegisters = bloodRegisterRepository.findAll();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền truy xuất danh sách đơn đăng ký hiến máu");
        }
        return bloodRegisters.stream()
                .map(bloodRegister -> BloodRegisterListResponse.builder()
                        .id(bloodRegister.getId())
                        .fullName(bloodRegister.getUser().getFullName())
                        .wantedDate(bloodRegister.getWantedDate())
                        .wantedHour(bloodRegister.getWantedHour())
                        .status(bloodRegister.getStatus())
                        .bloodType(bloodRegister.getUser().getBloodType())
                        .healthCheckStatus(bloodRegister.getHealthCheck() != null && bloodRegister.getHealthCheck().isStatus())
                        .build())
                .collect(Collectors.toList());
    }

    public BloodRegisterResponse create(BloodRegisterRequest bloodRegisterRequest) {
//        User
        User currentUser = authenticationService.getCurrentUser();

        LocalDate lastDonation = currentUser.getLastDonation();
        if (lastDonation != null) {
            LocalDate nextEligibleDate = lastDonation.plusDays(84);
            if (LocalDate.now().isBefore(nextEligibleDate)) {
                long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), nextEligibleDate);
                throw new GlobalException("Bạn chưa đủ thời gian để hiến máu tiếp theo. Vui lòng chờ thêm " + remainingDays + " ngày.");
            }
        }

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

        // Create notification for blood donation registration
        String donationMessage = "Blood type: " + currentUser.getBloodType() +
                ", Date: " + bloodRegisterRequest.getWantedDate() +
                ", Time: " + bloodRegisterRequest.getWantedHour();
        notificationService.createBloodRequestNotification(currentUser, "Blood donation registration: " + donationMessage);

// BloodRegisterResponse
        BloodRegisterResponse bloodRegisterResponse = BloodRegisterResponse.builder()
                .id(bloodRegister.getUser().getId())
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
            // Chỉ update ngày và giờ mong muốn
            bloodRegister.setWantedDate(bloodRegisterRequest.getWantedDate());
            bloodRegister.setWantedHour(bloodRegisterRequest.getWantedHour());
            bloodRegisterRepository.save(bloodRegister);
            // Chuẩn bị response với dữ liệu mới cập nhật
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
        } else {
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
                if(bloodRegister.getStatus().equals(BloodRegisterStatus.PENDING)){
                    bloodRegister.setStatus(BloodRegisterStatus.APPROVED);
                    bloodRegisterRepository.save(bloodRegister);
                    notificationService.createSystemAnnouncementNotification(
                            bloodRegister.getUser(),
                            "Blood Donation Approved",
                            "Your blood donation registration has been approved. Please arrive at the scheduled time."
                    );

                }else{
                    throw new GlobalException("Đơn đăng ký không ở trạng thái chờ duyệt");
                }

            }
            case REJECTED -> {
                if (authenticationService.getCurrentUser().getRole() != Role.ADMIN) {
                    throw new GlobalException("Bạn không có quyền từ chối đơn đăng ký");
                }
                if(bloodRegister.getStatus().equals(BloodRegisterStatus.PENDING)){
                    bloodRegister.setStatus(BloodRegisterStatus.REJECTED);
                    bloodRegisterRepository.save(bloodRegister);
                    notificationService.createSystemAnnouncementNotification(
                            bloodRegister.getUser(),
                            "Blood Donation Rejected",
                            "Your blood donation registration has been rejected. Please contact support for more information."
                    );
                }else{
                    throw new GlobalException("Đơn đăng ký không ở trạng thái đã duyệt");
                }

            }
            case CANCELED -> {
                User currentUser = authenticationService.getCurrentUser();
                if (!bloodRegister.getUser().getEmail().equals(currentUser.getEmail()) && currentUser.getRole() != Role.MEMBER) {
                    throw new GlobalException("Bạn không có quyền hủy đơn đăng ký này");
                }
                if(bloodRegister.getStatus().equals(BloodRegisterStatus.PENDING)){
                    bloodRegister.setStatus(BloodRegisterStatus.CANCELED);
                    bloodRegisterRepository.save(bloodRegister);
                    notificationService.createSystemAnnouncementNotification(
                            bloodRegister.getUser(),
                            "Blood Donation Cancelled",
                            "Your blood donation registration has been cancelled."    );

                }else{
                    throw new GlobalException("Đơn đăng ký không ở trạng thái chờ duyệt");
                }


            }
            default -> throw new GlobalException("Trạng thái không hợp lệ");
        }

    }


    public List<BloodRegisterListResponse> getByStatuses(List<BloodRegisterStatus> statuses) {
        List<BloodRegister> bloodRegisters = bloodRegisterRepository.findByStatusIn(statuses);

        return bloodRegisters.stream()
                .map(bloodRegister -> BloodRegisterListResponse.builder()
                        .id(bloodRegister.getId())
                        .fullName(bloodRegister.getUser().getFullName())
                        .wantedDate(bloodRegister.getWantedDate())
                        .wantedHour(bloodRegister.getWantedHour())
                        .status(bloodRegister.getStatus())
                        .bloodType(bloodRegister.getUser().getBloodType())
                        .build())
                .collect(Collectors.toList());
    }

    public List<DonationHistoryResponse> getHistoryByUserId(Long userId) {
        List<BloodRegister> bloodRegisters = bloodRegisterRepository.findByUserId(userId);

        if (bloodRegisters.isEmpty()) {
            throw new GlobalException("Không tìm thấy lịch sử đăng ký hiến máu cho người dùng này");
        }

        return bloodRegisters.stream()
                .filter(bloodRegister -> bloodRegister.getStatus() == BloodRegisterStatus.COMPLETED)
                .map(bloodRegister -> {
                    Optional<BloodDonationHistory> bloodOpt = bloodDonationHistoryRepository.findByBloodRegisterId(bloodRegister.getId());
                    float unit = bloodOpt.map(BloodDonationHistory::getUnit).orElse(0f);
                    LocalDate completedDate = bloodOpt.map(BloodDonationHistory::getDonationDate).orElse(null);

                    return DonationHistoryResponse.builder()
                            .id(bloodRegister.getUser().getId())
                            .fullName(bloodRegister.getUser().getFullName())
                            .bloodType(bloodRegister.getUser().getBloodType())
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
                        Optional<BloodDonationHistory> blood = bloodDonationHistoryRepository.findByBloodRegisterId(bloodRegister.getId());
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

            // Kiểm tra đã có health check chưa
            if (bloodRegister.getHealthCheck() == null) {
                throw new GlobalException("Đơn đăng ký chưa có kiểm tra sức khỏe, không thể hoàn thành");
            }

            if (bloodRegister.getHealthCheck().isStatus()) {
                LocalDate checkDate = bloodRegister.getHealthCheck().getCheckDate();
                LocalDate implementationDate = bloodSetCompletedRequest.getImplementationDate();

                if (implementationDate.isBefore(checkDate)) {
                    throw new GlobalException("Ngày hiến máu không được trước ngày kiểm tra sức khỏe");
                }
                // 2. Bổ sung vào kho máu
                BloodInventory bloodInventory = new BloodInventory();
                bloodInventory.setBloodType(bloodRegister.getUser().getBloodType());
                bloodInventory.setUnitsAvailable(bloodSetCompletedRequest.getUnit());
                bloodInventory.setStatus(BloodInventoryStatus.AVAILABLE);
                bloodInventoryRepository.save(bloodInventory);

                // 3. Tạo bản ghi máu mới
                BloodDonationHistory bloodDonationHistory = BloodDonationHistory.builder()
                        .bloodType(bloodRegister.getUser().getBloodType())
                        .unit(bloodSetCompletedRequest.getUnit())
                        .expirationDate(bloodSetCompletedRequest.getImplementationDate().plusDays(50))
                        .donationDate(bloodSetCompletedRequest.getImplementationDate())
                        .bloodRegister(bloodRegister)
                        .bloodInventory(bloodInventory)
                        .build();
                bloodDonationHistoryRepository.save(bloodDonationHistory);

                // 4. Cập nhật trạng thái đơn đăng ký
                bloodRegister.setStatus(BloodRegisterStatus.COMPLETED);
                bloodRegisterRepository.save(bloodRegister);

                User user = bloodRegister.getUser();
                user.setLastDonation(bloodSetCompletedRequest.getImplementationDate());
                authenticationRepository.save(user);



                // 5. Chuyển sang response trả về
                return BloodRegisterResponse.builder()
                        .id(bloodRegister.getUser().getId())
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
            } else {
                bloodRegister.setStatus(BloodRegisterStatus.INCOMPLETED);
                bloodRegisterRepository.save(bloodRegister);
                throw new GlobalException("Đơn đăng ký không đủ điều kiện để hoàn thành");
            }
        }catch (GlobalException e) {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("Đơn đã hoàn thành hoặc không tồn tại");
        }
    }


    public List<BloodRegisterHistoryResponse> getListDonation() {
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

                    return BloodRegisterHistoryResponse.builder()
                            .id(user.getId())
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

    public List<Map<String, Object>> getMonthlyCompletedCount(int year) {
        List<Object[]> rawData = bloodRegisterRepository.countCompletedRegistersPerMonth(year);

        Map<Integer, Long> resultMap = new HashMap<>();
        for (Object[] row : rawData) {
            Integer month = ((Number) row[0]).intValue();
            Long total = ((Number) row[1]).longValue();
            resultMap.put(month, total);
        }

        List<Map<String, Object>> finalResult = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", month);
            item.put("totalCompletedRequests", resultMap.getOrDefault(month, 0L));
            finalResult.add(item);
        }

        return finalResult;
    }

    ///thông báo xin máu /////
    public int inviteEligibleUsers(String bloodType) {

        BloodType typeEnum;
        //phân role
        User currentUser = authenticationService.getCurrentUser();
        if (!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền gửi lời mời hiến máu");
        }
        try {
            typeEnum = BloodType.valueOf(bloodType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nhóm máu không hợp lệ: " + bloodType);
        }

        List<User> allUsers = authenticationRepository.findByBloodType(typeEnum);

        LocalDate now = LocalDate.now();
        List<User> eligibleUsers = allUsers.stream()
                .filter(user -> user.getLastDonation() == null ||
                        Period.between(user.getLastDonation(), now).getMonths() >= 3)
                .toList();

        if (eligibleUsers.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy người dùng phù hợp với nhóm máu " + bloodType);
        }

        String title = "Lời mời hiến máu";
        String message = "Bạn có nhóm máu phù hợp và đủ điều kiện để hiến máu. Hãy tham gia để cứu người!";
        int count = 0;
        for (User user : eligibleUsers) {
            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .type(NotificationType.BLOOD_DONATION_INVITE)
                    .recipient(user)
                    .read(false)
                    .build();
            notificationRepository.save(notification);
            count++;
        }
        return count;
    }


}