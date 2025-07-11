package com.example.demo.service;

import com.example.demo.dto.request.CertififcateRequest;
import com.example.demo.dto.response.CertificateResponse;
import com.example.demo.entity.Certificate;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.exception.exceptions.GlobalException;
import com.example.demo.repository.AuthenticationRepository;
import com.example.demo.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CertificateService {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    CertificateRepository certificateRepository;

    public CertificateResponse create(CertififcateRequest request) {
        // Lấy nhân viên hiện tại
        User currentStaff = authenticationService.getCurrentUser();
        if (!currentStaff.getRole().equals(Role.STAFF)) {
            throw new GlobalException("Bạn không có quyền tạo chứng nhận hiến máu");
        }
        User donor = authenticationRepository.findUserByEmail(request.getDonorEmail());
        if(donor == null) {
            throw new GlobalException("Người hiến máu không tồn tại");
        }

        // Tạo chứng nhận
        Certificate certificate = Certificate.builder()
                .donor(donor)
                .staff(currentStaff)
                .issueDate(request.getIssueDate())
                .build();

        certificate = certificateRepository.save(certificate);

        // Trả về response
        return CertificateResponse.builder()
                .id(certificate.getId())
                .donorName(donor.getFullName())
                .staffName(currentStaff.getFullName())
                .issueDate(certificate.getIssueDate())
                .build();
    }

    public CertificateResponse getCertificateById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new GlobalException("Chứng nhận hiến máu không tồn tại"));

        return CertificateResponse.builder()
                .id(certificate.getId())
                .donorName(certificate.getDonor().getFullName())
                .staffName(certificate.getStaff().getFullName())
                .issueDate(certificate.getIssueDate())
                .build();
    }
}
