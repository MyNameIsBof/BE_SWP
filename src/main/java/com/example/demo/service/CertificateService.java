package com.example.demo.service;

import com.example.demo.dto.request.CertificateRequest;
import com.example.demo.dto.response.CertificateResponse;
import com.example.demo.entity.BloodRegister;
import com.example.demo.entity.Certificate;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.BloodRegisterRepository;
import com.example.demo.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    BloodRegisterRepository bloodRegisterRepository;

    @Autowired
    CertificateRepository certificateRepository;

    public CertificateResponse create(CertificateRequest request) {
        // Lấy nhân viên hiện tại
        User currentStaff = authenticationService.getCurrentUser();
        if (!currentStaff.getRole().equals(Role.STAFF)) {
            throw new GlobalException("Bạn không có quyền tạo chứng nhận hiến máu");
        }

        // Lấy đơn đăng ký hiến máu
        BloodRegister bloodRegister = bloodRegisterRepository.findById(request.getBloodRegisterId())
                .orElseThrow(() -> new GlobalException("Không tìm thấy đơn đăng ký hiến máu"));

        // Kiểm tra trạng thái đơn phải là COMPLETED thì mới được cấp chứng nhận
        if (!bloodRegister.getStatus().name().equals("COMPLETED")) {
            throw new GlobalException("Chỉ cấp chứng nhận cho các đơn đã hoàn thành");
        }

        // Kiểm tra đã có chứng nhận chưa
        if (bloodRegister.getCertificate() != null) {
            throw new GlobalException("Đơn đăng ký này đã được cấp chứng nhận");
        }

        // Tạo chứng nhận
        Certificate certificate = Certificate.builder()
                .issueDate(request.getIssueDate())
                .bloodRegister(bloodRegister)
                .staff(currentStaff)
                .build();

        certificate = certificateRepository.save(certificate);

        // Trả về response
        return CertificateResponse.builder()
                .id(certificate.getId())
                .issueDate(certificate.getIssueDate())
                .donorName(bloodRegister.getUser().getFullName())
                .staffName(currentStaff.getFullName())
                .build();
    }


    public List<CertificateResponse> getCertificatesByDonorId(Long donorId) {
        List<Certificate> certificates = certificateRepository.findAllByBloodRegisterByUserId(donorId);

        return certificates.stream()
                .map(c -> CertificateResponse.builder()
                        .id(c.getId())
                        .donorName(c.getBloodRegister().getUser().getFullName())
                        .staffName(c.getStaff().getFullName())
                        .issueDate(c.getIssueDate())
                        .build())
                .toList();
    }

}
