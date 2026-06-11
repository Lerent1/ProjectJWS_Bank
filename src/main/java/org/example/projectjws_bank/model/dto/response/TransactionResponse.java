package org.example.projectjws_bank.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private BigDecimal amount;
    private String type; // CREDIT / DEBIT
    private LocalDateTime createdAt;
}
