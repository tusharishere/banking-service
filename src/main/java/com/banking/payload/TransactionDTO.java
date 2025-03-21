package com.banking.payload;

import com.banking.entity.enums.TransactionStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long id;

    private String transactionReference;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be positive")
    private BigDecimal amount;

    private LocalDateTime transactionDate;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    private Long debitCardId;

    private Long creditCardId;

    private TransactionStatus status;

    private LocalDateTime transactionTimestamp;

}
