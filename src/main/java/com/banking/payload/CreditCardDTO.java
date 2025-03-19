package com.banking.payload;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDTO {
    private Long id;

    private String cardNumber;

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotBlank(message = "Cardholder name is required")
    private String cardholderName;

    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    private String cvv;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Credit limit must be positive")
    private BigDecimal creditLimit;

    private BigDecimal availableCredit;

    private boolean active;

    private List<Long> transactionIds;
}
