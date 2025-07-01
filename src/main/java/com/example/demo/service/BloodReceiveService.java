package com.example.demo.service;

import com.example.demo.dto.request.BloodReceiveRequest;
import com.example.demo.dto.request.BloodSetCompletedRequest;
import com.example.demo.dto.response.BloodReceiveResponse;
import com.example.demo.dto.response.BloodReceiveListResponse;
import com.example.demo.dto.response.BloodRegisterListResponse;
import com.example.demo.entity.BloodInventory;
import com.example.demo.entity.BloodReceive;
import com.example.demo.entity.BloodRegister;
import com.example.demo.entity.User;
import com.example.demo.enums.BloodReceiveStatus;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.mapper.BloodReceiveMapper;
import com.example.demo.repository.BloodInventoryRepository;
import com.example.demo.repository.BloodReceiveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloodReceiveService {
    private final BloodReceiveRepository bloodReceiveRepository;
    private final BloodReceiveMapper bloodReceiveMapper;
    private final AuthenticationService authenticationService;
    private final BloodInventoryRepository bloodInventoryRepository;
    private final NotificationService notificationService;
    private final EmailNotificationService emailNotificationService;


    public List<BloodReceiveListResponse> getAll() {
        List<BloodReceive> bloodReceives = bloodReceiveRepository.findAll();
        User currentUser = authenticationService.getCurrentUser();
          if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền truy xuất danh sách đơn đăng ký nhận máu");
        }
        return bloodReceives.stream()
                .map(bloodReceive -> BloodReceiveListResponse.builder()  // Sử dụng lambda để tạo đối tượng
                        .id(bloodReceive.getId())
                        .status(bloodReceive.getStatus())  // Chuyển trạng thái (enum)
                        .wantedDate(bloodReceive.getWantedDate())  // Ngày mong muốn
                        .wantedHour(bloodReceive.getWantedHour())  // Giờ mong muốn
                        .bloodType(bloodReceive.getUser().getBloodType())  // Loại nhóm máu
                        .isEmergency(bloodReceive.isEmergency())  // Kiểm tra tình trạng khẩn cấp
                        .build())
                .toList();
    }


public List<BloodReceiveListResponse> getByStatuses(List<BloodReceiveStatus> statuses) {
    List<BloodReceive> bloodReceives = bloodReceiveRepository.findByStatusIn(statuses);
    User currentUser = authenticationService.getCurrentUser();
         if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền truy xuất danh sách đơn đăng ký nhận máu");
        }
    return bloodReceives.stream()
            .map(bloodReceive -> BloodReceiveListResponse.builder()  // Sử dụng lambda để tạo đối tượng
                    .id(bloodReceive.getId())
                    .status(bloodReceive.getStatus())  // Chuyển trạng thái (enum)
                    .wantedDate(bloodReceive.getWantedDate())  // Ngày mong muốn
                    .wantedHour(bloodReceive.getWantedHour())  // Giờ mong muốn
                    .bloodType(bloodReceive.getUser().getBloodType())  // Loại nhóm máu
                    .isEmergency(bloodReceive.isEmergency())  // Kiểm tra tình trạng khẩn cấp
                    .build())
            .toList();
}

    @Transactional
    public BloodReceiveResponse create(BloodReceiveRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        // Update user information
        currentUser.setWeight(request.getWeight());
        currentUser.setHeight(request.getHeight());
        currentUser.setBirthdate(request.getBirthdate());
        currentUser.setLastDonation(request.getLastDonation());
        currentUser.setMedicalHistory(request.getMedicalHistory());
        currentUser.setBloodType(request.getBloodType());
        currentUser.setEmergencyName(request.getEmergencyName());
        currentUser.setEmergencyPhone(request.getEmergencyPhone());


        // Create blood receive record
        BloodReceive bloodReceive = bloodReceiveMapper.toBloodReceive(request);
        bloodReceive.setStatus(BloodReceiveStatus.PENDING); // Using BloodRegisterStatus from entity
        bloodReceive.setUser(currentUser);
        bloodReceive.setEmergency(request.isEmergency());
        bloodReceive.setWantedDate(request.getWantedDate());
        bloodReceive.setWantedHour(request.getWantedHour());
        bloodReceiveRepository.save(bloodReceive);

        return createResponseFromUserAndReceive(currentUser, bloodReceive);
    }

    @Transactional
    public BloodReceiveResponse update(Long id, BloodReceiveRequest request) {
        BloodReceive bloodReceive = bloodReceiveRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException("Đơn yêu cầu không tồn tại"));

        if (bloodReceive.getStatus() == BloodReceiveStatus.PENDING) {
            User currentUser = authenticationService.getCurrentUser();

            if (!bloodReceive.getUser().getEmail().equals(currentUser.getEmail())) {
                throw new AuthenticationException("Bạn không có quyền cập nhật đơn yêu cầu này");
            }

            // Update user information
            currentUser.setWeight(request.getWeight());
            currentUser.setHeight(request.getHeight());
            currentUser.setBirthdate(request.getBirthdate());
            currentUser.setLastDonation(request.getLastDonation());
            currentUser.setMedicalHistory(request.getMedicalHistory());
            currentUser.setBloodType(request.getBloodType());
            currentUser.setEmergencyName(request.getEmergencyName());
            currentUser.setEmergencyPhone(request.getEmergencyPhone());

            bloodReceive.setWantedHour(request.getWantedHour());
            bloodReceiveRepository.save(bloodReceive);

            return createResponseFromUserAndReceive(currentUser, bloodReceive);
        } else {
            throw new AuthenticationException("Đơn yêu cầu đã được xử lý và không thể cập nhật");
        }
    }

    @Transactional
    public void updateStatus(Long id, BloodReceiveStatus status) {
        BloodReceive bloodReceive = bloodReceiveRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException("Đơn yêu cầu không tồn tại"));

        User currentUser = authenticationService.getCurrentUser();

        switch (status) {
            case APPROVED:
                if (currentUser.getRole() != Role.ADMIN) {
                    throw new AuthenticationException("Bạn không có quyền duyệt đơn");
                }
                bloodReceive.setStatus(BloodReceiveStatus.APPROVED);
                
                // Send appointment confirmation notification
                String appointmentDetails = String.format("Blood Receive Date: %s at %s", 
                    bloodReceive.getWantedDate(), bloodReceive.getWantedHour());
                notificationService.createAppointmentConfirmation(bloodReceive.getUser(), appointmentDetails);
                emailNotificationService.sendAppointmentConfirmationEmail(bloodReceive.getUser(), appointmentDetails);
                break;
            case REJECTED:
                if (currentUser.getRole() != Role.ADMIN) {
                    throw new AuthenticationException("Bạn không có quyền từ chối đơn");
                }
                bloodReceive.setStatus(BloodReceiveStatus.REJECTED);
                break;
            case COMPLETED:
                if (currentUser.getRole() != Role.STAFF) {
                    throw new AuthenticationException("Bạn không có quyền đánh dấu đơn hoàn thành");
                }
                bloodReceive.setStatus(BloodReceiveStatus.COMPLETED);
                break;
            case INCOMPLETED:
                if (currentUser.getRole() != Role.STAFF) {
                    throw new AuthenticationException("Bạn không có quyền đánh dấu đơn chưa hoàn thành");
                }
                bloodReceive.setStatus(BloodReceiveStatus.INCOMPLETED);
                break;
            case CANCELED:
                if (!bloodReceive.getUser().getEmail().equals(currentUser.getEmail()) &&
                        currentUser.getRole() != Role.MEMBER) {
                    throw new AuthenticationException("Bạn không có quyền hủy đơn này");
                }
                bloodReceive.setStatus(BloodReceiveStatus.CANCELED);
                break;
            default:
                throw new AuthenticationException("Trạng thái không hợp lệ");
        }

        bloodReceiveRepository.save(bloodReceive);
    }

    public BloodReceiveResponse getById(Long id) {
        User currentUser = authenticationService.getCurrentUser();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền xem thông tin đơn yêu cầu này");
        }
        BloodReceive bloodReceive = bloodReceiveRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException("Đơn yêu cầu không tồn tại"));

        return createResponseFromUserAndReceive(bloodReceive.getUser(), bloodReceive);
    }

    private BloodReceiveResponse createResponseFromUserAndReceive(User user, BloodReceive bloodReceive) {
        return BloodReceiveResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .gender(user.getGender())
                .birthdate(user.getBirthdate())
                .height(user.getHeight())
                .weight(user.getWeight())
                .lastDonation(user.getLastDonation())
                .medicalHistory(user.getMedicalHistory())
                .bloodType(user.getBloodType())
                .wantedDate(bloodReceive.getWantedDate())
                .wantedHour(bloodReceive.getWantedHour())
                .emergencyName(user.getEmergencyName())
                .emergencyPhone(user.getEmergencyPhone())
                .isEmergency(bloodReceive.isEmergency())
                .build();
    }



    @Transactional
    public BloodReceiveResponse setCompleted(BloodSetCompletedRequest request) {
        User currentUser = authenticationService.getCurrentUser();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền lấy máu cho người nhận");
        }
        BloodType neededType = currentUser.getBloodType();
        float requiredUnits = request.getUnit();

        // Lấy danh sách nhóm máu tương thích với người nhận
        List<BloodType> compatibleTypes = BloodType.CompatibleBloodMap.get(neededType);
        if (compatibleTypes == null || compatibleTypes.isEmpty()) {
            throw new GlobalException("Không có nhóm máu phù hợp để truyền cho " + neededType);
        }

        // Lấy danh sách kho máu theo nhóm tương thích, còn máu (>0 đơn vị)
        List<BloodInventory> inventories = bloodInventoryRepository
                .findByBloodTypeInAndUnitsAvailableGreaterThan(compatibleTypes, 0f);

        // Ưu tiên loại máu gần với nhóm máu người nhận nhất
        inventories.sort(Comparator.comparingInt(
                inv -> compatibleTypes.indexOf(inv.getBloodType())
        ));

        float collected = 0f;
        List<BloodInventory> usedInventories = new ArrayList<>();

        for (BloodInventory inv : inventories) {
            if (collected >= requiredUnits) break;

            float available = inv.getUnitsAvailable();
            float used = Math.min(available, requiredUnits - collected);

            inv.setUnitsAvailable(available - used);
            collected += used;
            usedInventories.add(inv);
        }

        if (collected < requiredUnits) {
            throw new GlobalException("Không đủ máu để truyền (đã có " + collected + " / cần " + requiredUnits + ")");
        }

        bloodInventoryRepository.saveAll(usedInventories);

        // Đánh dấu đơn yêu cầu nhận máu là hoàn tất
        BloodReceive receive = bloodReceiveRepository.findById(request.getBloodId())
                .orElseThrow(() -> new GlobalException("Đơn yêu cầu nhận máu không tồn tại"));
        receive.setStatus(BloodReceiveStatus.COMPLETED);
        bloodReceiveRepository.save(receive);

        return createResponseFromUserAndReceive(currentUser, receive);
    }

    public List<BloodReceiveListResponse> getByUserId(Long userId) {
        User currentUser = authenticationService.getCurrentUser();
        if(!Role.STAFF.equals(currentUser.getRole()) && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new GlobalException("Bạn không có quyền truy xuất danh sách đơn đăng ký hiến máu của người dùng");
        }
        List<BloodReceive> bloodReceives = bloodReceiveRepository.findByUserId(userId);

        if (bloodReceives.isEmpty()) {
            throw new GlobalException("Không tìm thấy đơn nhận máu nào cho người dùng này");
        }

        return bloodReceives.stream()
                .map(bloodReceive -> BloodReceiveListResponse.builder()
                        .id(bloodReceive.getId())
                        .status(bloodReceive.getStatus())
                        .wantedDate(bloodReceive.getWantedDate())
                        .wantedHour(bloodReceive.getWantedHour())
                        .status(bloodReceive.getStatus())
                        .bloodType(bloodReceive.getUser().getBloodType())
                        .isEmergency(bloodReceive.isEmergency())
                        .build()
                ).collect(Collectors.toList());
    }
}