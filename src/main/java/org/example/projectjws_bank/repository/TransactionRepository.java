package org.example.projectjws_bank.repository;

import org.example.projectjws_bank.model.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromAccount_IdOrToAccount_Id(
            Long fromId, Long toId, Pageable pageable);
}
