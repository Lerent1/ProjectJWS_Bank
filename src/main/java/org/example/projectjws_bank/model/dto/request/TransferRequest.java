package org.example.projectjws_bank.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "Khong duoc de trong")
    private String from;

    @NotBlank(message = "Khong duoc de trong")
    private String to;

    @NotNull(message = "Khong duoc de trong")
    @Positive
    private BigDecimal amount;
}
