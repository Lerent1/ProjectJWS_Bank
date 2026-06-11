package org.example.projectjws_bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.model.dto.request.UserUpdateRequest;
import org.example.projectjws_bank.model.dto.response.UserResponse;
import org.example.projectjws_bank.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<UserResponse> result = userService.getAllUsers(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Lay danh sach user thanh cong");
        response.put("data", result.getContent());
        response.put("currentPage", result.getNumber());
        response.put("totalItems", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable Long id) {

        UserResponse user = userService.getUserById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Lay user thanh cong");
        response.put("data", user);

        return ResponseEntity.ok(response);
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse updated = userService.updateUser(id, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cap nhat thanh cong");
        response.put("data", updated);

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Xoa thanh cong");

        return ResponseEntity.ok(response);
    }
}