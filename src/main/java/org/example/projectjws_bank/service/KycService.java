package org.example.projectjws_bank.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.exception.BadRequestException;
import org.example.projectjws_bank.exception.NotFoundException;
import org.example.projectjws_bank.model.dto.response.KycResponse;
import org.example.projectjws_bank.model.entity.KycProfile;
import org.example.projectjws_bank.model.entity.User;
import org.example.projectjws_bank.model.entity.enums.KycAction;
import org.example.projectjws_bank.model.entity.enums.KycStatus;
import org.example.projectjws_bank.repository.KycRepository;
import org.example.projectjws_bank.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KycService {
    private final KycRepository kycRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    // UPLOAD
    public KycResponse uploadKyc(
            MultipartFile file,
            String documentType) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User khong ton tai"));

        // validate
        if (file.isEmpty()) {
            throw new BadRequestException("File khong duoc rong");
        }

        if (documentType == null || documentType.isBlank()) {
            throw new BadRequestException("documentType khong duoc rong");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") &&
                        !contentType.equals("application/pdf"))) {
            throw new BadRequestException("Chi chap nhan anh hoac PDF");
        }

        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "kyc",
                    "resource_type", "auto"
            );

            Map<String, Object> uploadResult =
                    cloudinary.uploader().upload(file.getBytes(), options);

            String url = uploadResult.get("secure_url").toString();

            KycProfile kyc = KycProfile.builder()
                    .documentUrl(url)
                    .documentType(documentType)
                    .status(KycStatus.PENDING)
                    .submittedAt(LocalDateTime.now())
                    .user(user)
                    .build();

            kycRepository.save(kyc);

            return mapToResponse(kyc);

        } catch (Exception e) {
            throw new RuntimeException("Upload file that bai: " + e.getMessage());
        }
    }

    // APPROVE
    @Transactional
    public KycResponse approveKyc(Long id, KycAction action) {

        KycProfile kyc = kycRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("KYC khong tim thay"));

        User user = kyc.getUser();

        if (action == KycAction.APPROVE) {
            kyc.setStatus(KycStatus.CONFIRM);
            user.setIsKyc(true);
        } else {
            kyc.setStatus(KycStatus.REJECT);
        }

        kycRepository.save(kyc);
        userRepository.save(user);

        return mapToResponse(kyc);
    }

    private KycResponse mapToResponse(KycProfile kyc) {
        return KycResponse.builder()
                .id(kyc.getId())
                .documentUrl(kyc.getDocumentUrl())
                .documentType(kyc.getDocumentType())
                .status(kyc.getStatus().name())
                .build();
    }
}