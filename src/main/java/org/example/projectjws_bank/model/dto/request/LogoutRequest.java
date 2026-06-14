package org.example.projectjws_bank.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank(message = "Refresh token khong duoc de trong")
    private String refreshToken;

    @NotBlank(message = "Access token khong duoc de trong")
    private String accessToken;
}