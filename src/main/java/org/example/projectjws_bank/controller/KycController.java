package org.example.projectjws_bank.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.exception.BadRequestException;
import org.example.projectjws_bank.model.dto.response.KycResponse;
import org.example.projectjws_bank.model.entity.enums.KycAction;
import org.example.projectjws_bank.service.KycService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    // UPLOAD KYC
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadKyc(
            @RequestParam MultipartFile file,
            @RequestParam Long userId,
            @RequestParam String documentType) {

        // validate file
        if (file.isEmpty()) {
            throw new BadRequestException("File khong duoc rong");
        }

        KycResponse kyc = kycService.uploadKyc(file, userId, documentType);

        return buildResponse("Upload KYC thanh cong", kyc);
    }

    // APPROVE / REJECT
    @PutMapping("/{id}/approval")
    public ResponseEntity<Map<String, Object>> approveKyc(
            @PathVariable Long id,
            @RequestParam KycAction action) {

        KycResponse kyc = kycService.approveKyc(id, action);

        String message = action == KycAction.APPROVE
                ? "Duyet KYC thanh cong"
                : "Tu choi KYC";

        return buildResponse(message, kyc);
    }

    // COMMON RESPONSE
    private ResponseEntity<Map<String, Object>> buildResponse(
            String message, Object data) {

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}