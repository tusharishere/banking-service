package com.banking.repository;

import com.banking.entity.DebitCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DebitCardRepository extends JpaRepository<DebitCard, Long> {
    Optional<DebitCard> findByCardNumber(String cardNumber);
    boolean existsByCardNumber(String cardNumber);
    Optional<DebitCard> findByAccountId(Long accountId);
    boolean existsByAccountId(Long accountId);
}