package org.example.projectjws_bank.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.projectjws_bank.exception.BadRequestException;
import org.example.projectjws_bank.exception.NotFoundException;
import org.example.projectjws_bank.model.dto.response.AccountResponse;
import org.example.projectjws_bank.model.entity.Account;
import org.example.projectjws_bank.model.entity.Transaction;
import org.example.projectjws_bank.repository.AccountRepository;
import org.example.projectjws_bank.repository.TransactionRepository;
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

    public AccountResponse getAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        return toResponse(account);
    }

    public BigDecimal getBalance(String accountNumber) {
        Account account = findAccount(accountNumber);
        return account.getBalance();
    }

    private Account findAccount(String accountNumber) {
        return accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Khong tim thay account"));
    }

    private AccountResponse toResponse(Account account) {

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .active(account.getActive())
                .userId(account.getUser().getId())
                .build();
    }

    @Transactional
    public void transfer(String fromAcc, String toAcc, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("So tien khong hop le");
        }

        if (fromAcc.equals(toAcc)) {
            throw new BadRequestException("Khong the chuyen cung tai khoan");
        }

        Account from = accountRepository.findByAccountNumber(fromAcc)
                .orElseThrow(() -> new NotFoundException("From account Khong tim thay"));

        Account to = accountRepository.findByAccountNumber(toAcc)
                .orElseThrow(() -> new NotFoundException("To account Khong tim thay"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Khong du so du");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = Transaction.builder()
                .amount(amount)
                .description("Transfer from " + fromAcc + " to " + toAcc)
                .fromAccount(from)
                .toAccount(to)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(tx);
    }

    public void changePin(String accountNumber, String oldPin, String newPin) {

        Account acc = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Khong tim thay Account"));

        if (!passwordEncoder.matches(oldPin, acc.getPinCode())) {
            throw new BadRequestException("Sai PIN cu");
        }

        acc.setPinCode(passwordEncoder.encode(newPin));

        accountRepository.save(acc);
    }
}