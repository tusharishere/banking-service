package com.banking.repository;

import com.banking.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByCardNumber(String cardNumber);
    boolean existsByCardNumber(String cardNumber);
    Optional<CreditCard> findByAccountId(Long accountId);
    boolean existsByAccountId(Long accountId);
}