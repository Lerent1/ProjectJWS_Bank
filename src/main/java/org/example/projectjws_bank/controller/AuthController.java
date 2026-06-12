package org.example.projectjws_bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.model.dto.request.*;
import org.example.projectjws_bank.model.dto.response.AuthResponse;
import org.example.projectjws_bank.model.dto.response.RegisterResponse;
import org.example.projectjws_bank.service.AuthService;
import org.example.projectjws_bank.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse res = userService.register(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dang ky thanh cong");
        response.put("data", res);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse auth = authService.login(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Dang nhap thanh cong");
        response.put("data", auth);

        return ResponseEntity.ok(response);
    }

    // REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse res = authService.refreshToken(request.getRefreshToken());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Refresh token thanh cong");
        response.put("data", res);

        return ResponseEntity.ok(response);
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> req) {

        String refreshToken = req.get("refreshToken");

        authService.logout(refreshToken);

        return ResponseEntity.ok("Logout thanh cong");
    }
}