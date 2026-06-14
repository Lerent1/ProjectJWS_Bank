package org.example.projectjws_bank.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @NotBlank(message = "Khong duoc de trong")
    private String username;

    @Email(message = "Email khong hop le")
    private String email;

    @NotBlank(message = "Khong duoc de trong")
    private String newPassword;
}