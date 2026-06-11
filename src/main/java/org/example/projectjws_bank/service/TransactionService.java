package org.example.projectjws_bank.service;

import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.model.dto.response.TransactionResponse;
import org.example.projectjws_bank.model.entity.Transaction;
import org.example.projectjws_bank.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public Page<TransactionResponse> getStatement(Long accountId, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return transactionRepository
                .findByFromAccount_IdOrToAccount_Id(accountId, accountId, pageable)
                .map(tx -> new TransactionResponse(
                        tx.getAmount(),
                        tx.getFromAccount().getId().equals(accountId)
                                ? "DEBIT"
                                : "CREDIT",
                        tx.getCreatedAt()
                ));
    }
}
