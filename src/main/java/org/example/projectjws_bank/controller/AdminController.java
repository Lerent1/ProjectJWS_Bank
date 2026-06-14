package org.example.projectjws_bank.controller;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.model.dto.request.RegisterRequest;
import org.example.projectjws_bank.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping("/createStaff")
    public ResponseEntity<Map<String, Object>> createStaff(
            @RequestBody RegisterRequest request) {

        var res = userService.createStaff(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Tao staff thanh cong");
        response.put("data", res);

        return ResponseEntity.ok(response);
    }
}
