package com.banking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_cards")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name ="card_number",nullable = false, unique = true)
    private String cardNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(name ="card_holder_name",nullable = false)
    private String cardholderName;

    @Column(name ="expiry_date",nullable = false)
    private LocalDate expiryDate;

    @Column(name ="cvv",nullable = false)
    private String cvv;

    @Column(name ="credit_limit",nullable = false)
    private BigDecimal creditLimit;

    @Column(name ="available_credit",nullable = false)
    private BigDecimal availableCredit;

    @Column(name ="active",nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

}