package org.example.projectjws_bank.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.model.dto.response.TransactionResponse;
import org.example.projectjws_bank.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Map<String, Object>> getStatement(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<TransactionResponse> result = transactionService.getStatement(accountId, page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Lay lich su giao dich thanh cong");
        response.put("data", result.getContent());
        response.put("currentPage", result.getNumber());
        response.put("totalItems", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());

        return ResponseEntity.ok(response);
    }
}