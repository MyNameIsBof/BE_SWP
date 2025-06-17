package com.example.demo.service;

import com.example.demo.dto.request.BloodReceiveRequest;
import com.example.demo.dto.response.BloodReceiveResponse;
import com.example.demo.entity.BloodReceive;
import com.example.demo.entity.User;
import com.example.demo.enums.BloodReceiveStatus;
import com.example.demo.enums.BloodRegisterStatus;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.AuthenticationException;
import com.example.demo.mapper.BloodReceiveMapper;
import com.example.demo.repository.BloodReceiveRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodReceiveService {
    private final BloodReceiveRepository bloodReceiveRepository;
    private final BloodReceiveMapper bloodReceiveMapper;
    private final AuthenticationService authenticationService;

    public List<BloodReceive> getAll() {
        return bloodReceiveRepository.findAll();
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
}