package com.banking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "debit_cards")
public class DebitCard {
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

    @Column(name = "active",nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "debitCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();


}