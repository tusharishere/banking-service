package com.banking.repository;

import com.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDebitCardId(Long debitCardId);
    List<Transaction> findByCreditCardId(Long creditCardId);
    Optional<Transaction> findByTransactionReference(String transactionReference);
    boolean existsByTransactionReference(String transactionReference);
}