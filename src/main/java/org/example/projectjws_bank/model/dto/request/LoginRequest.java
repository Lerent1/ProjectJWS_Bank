package org.example.projectjws_bank.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Khong duoc de trong")
    private String username;

    @NotBlank(message = "Khong duoc de trong")
    private String password;
}
