package org.example.projectjws_bank.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.exception.BadRequestException;
import org.example.projectjws_bank.exception.InsufficientBalanceException;
import org.example.projectjws_bank.exception.NotFoundException;
import org.example.projectjws_bank.model.dto.response.AccountResponse;
import org.example.projectjws_bank.model.entity.Account;
import org.example.projectjws_bank.model.entity.Transaction;
import org.example.projectjws_bank.repository.AccountRepository;
import org.example.projectjws_bank.repository.TransactionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    private Account getMyAccount(String accountNumber) {
        return accountRepository
                .findByAccountNumberAndUserUsername(accountNumber, getCurrentUsername())
                .orElseThrow(() -> new BadRequestException("Khong co quyen truy cap tai khoan"));
    }

    private Account getAnyAccount(String accountNumber) {
        return accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Khong tim thay account"));
    }

    public AccountResponse getAccount(String accountNumber) {
        return toResponse(getMyAccount(accountNumber));
    }

    public BigDecimal getBalance(String accountNumber) {
        return getMyAccount(accountNumber).getBalance();
    }

    @Transactional
    public void transfer(String fromAcc, String toAcc, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("So tien khong hop le");
        }

        if (fromAcc.equals(toAcc)) {
            throw new BadRequestException("Khong the chuyen cung tai khoan");
        }

        Account from = getMyAccount(fromAcc);
        Account to = getAnyAccount(toAcc);

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Khong du so du");
        }

        // UPDATE
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        // SAVE TRANSACTION
        transactionRepository.save(
                Transaction.builder()
                        .amount(amount)
                        .description("Transfer from " + fromAcc + " to " + toAcc)
                        .fromAccount(from)
                        .toAccount(to)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    public void changePin(String accountNumber, String oldPin, String newPin) {

        Account acc = getMyAccount(accountNumber);

        if (!passwordEncoder.matches(oldPin, acc.getPinCode())) {
            throw new BadRequestException("Sai PIN cu");
        }

        acc.setPinCode(passwordEncoder.encode(newPin));
        accountRepository.save(acc);
    }

    // MAPPER
    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .active(account.getActive())
                .userId(account.getUser().getId())
                .build();
    }
}