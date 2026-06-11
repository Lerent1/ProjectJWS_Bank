package org.example.projectjws_bank.service;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.exception.NotFoundException;
import org.example.projectjws_bank.model.dto.response.KycResponse;
import org.example.projectjws_bank.model.entity.KycProfile;
import org.example.projectjws_bank.model.entity.User;
import org.example.projectjws_bank.model.entity.enums.KycAction;
import org.example.projectjws_bank.model.entity.enums.KycStatus;
import org.example.projectjws_bank.repository.KycRepository;
import org.example.projectjws_bank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycRepository kycRepository;
    private final UserRepository userRepository;

    // ================== UPLOAD ==================
    public KycResponse uploadKyc(
            MultipartFile file,
            Long userId,
            String documentType) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User khong tim thay"));

        // validate file
        if (file.isEmpty()) {
            throw new RuntimeException("File khong duoc rong");
        }

        // giả lập upload
        String url = "http://localhost/files/" + file.getOriginalFilename();

        KycProfile kyc = KycProfile.builder()
                .documentUrl(url)
                .documentType(documentType)
                .status(KycStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .user(user)
                .build();

        kycRepository.save(kyc);

        return mapToResponse(kyc);
    }

    // ================== APPROVE ==================
    public KycResponse approveKyc(Long id, KycAction action) {

        KycProfile kyc = kycRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kyc khong tim thay"));

        if (action == KycAction.APPROVE) {
            kyc.setStatus(KycStatus.CONFIRM);
            kyc.getUser().setIsKyc(true);
        } else {
            kyc.setStatus(KycStatus.REJECT);
        }

        kycRepository.save(kyc);

        return mapToResponse(kyc);
    }

    // ================== MAPPER ==================
    private KycResponse mapToResponse(KycProfile kyc) {
        return KycResponse.builder()
                .id(kyc.getId())
                .documentUrl(kyc.getDocumentUrl())
                .documentType(kyc.getDocumentType())
                .status(kyc.getStatus().name())
                .build();
    }
}