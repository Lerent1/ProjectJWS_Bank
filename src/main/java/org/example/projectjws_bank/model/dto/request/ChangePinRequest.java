package org.example.projectjws_bank.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePinRequest {

    @NotBlank(message = "Khong duoc de trong")
    private String accountNumber;

    @NotBlank(message = "Khong duoc de trong")
    private String oldPin;

    @NotBlank(message = "Khong duoc de trong")
    private String newPin;
}
