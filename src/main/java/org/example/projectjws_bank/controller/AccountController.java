package org.example.projectjws_bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.model.dto.request.ChangePinRequest;
import org.example.projectjws_bank.model.dto.request.TransferRequest;
import org.example.projectjws_bank.model.dto.response.AccountResponse;
import org.example.projectjws_bank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Map<String, Object>> getAccount(
            @PathVariable String accountNumber) {

        AccountResponse account = accountService.getAccount(accountNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Lay thong tin tai khoan thanh cong");
        response.put("data", account);

        return ResponseEntity.ok(response);
    }

    // van tin so du
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(
            @PathVariable String accountNumber) {

        BigDecimal balance = accountService.getBalance(accountNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Lay so du thanh cong");
        response.put("accountNumber", accountNumber);
        response.put("balance", balance);

        return ResponseEntity.ok(response);
    }

    // chuyen tien
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transfer(
            @Valid @RequestBody TransferRequest request) {

        accountService.transfer(
                request.getFrom(),
                request.getTo(),
                request.getAmount()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Chuyen tien thanh cong");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/changePin")
    public ResponseEntity<Map<String, Object>> changePin(
            @Valid @RequestBody ChangePinRequest request) {

        accountService.changePin(
                request.getAccountNumber(),
                request.getOldPin(),
                request.getNewPin()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Doi PIN thanh cong");

        return ResponseEntity.ok(response);
    }
}