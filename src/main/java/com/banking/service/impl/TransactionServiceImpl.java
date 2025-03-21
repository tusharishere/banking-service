package com.banking.service.impl;

import com.banking.entity.CreditCard;
import com.banking.entity.DebitCard;
import com.banking.entity.Transaction;
import com.banking.exception.ResourceNotFoundException;
import com.banking.payload.TransactionDTO;
import com.banking.repository.CreditCardRepository;
import com.banking.repository.DebitCardRepository;
import com.banking.repository.TransactionRepository;
import com.banking.service.TransactionService;
import com.banking.entity.enums.TransactionType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final DebitCardRepository debitCardRepository;
    private final CreditCardRepository creditCardRepository;
    private final ModelMapper modelMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, DebitCardRepository debitCardRepository, CreditCardRepository creditCardRepository, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.debitCardRepository = debitCardRepository;
        this.creditCardRepository = creditCardRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        log.info("Creating transaction");
        transactionDTO.setTransactionReference(generateTransactionReference());
        transactionDTO.setTransactionTimestamp(LocalDateTime.now());

        Transaction transaction = prepareTransaction(transactionDTO);
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created successfully with reference: {}", transactionDTO.getTransactionReference());
        return mapToDTO(savedTransaction);
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        log.info("Fetching transaction by ID: {}", id);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transaction not found with ID: {}", id);
                    return new ResourceNotFoundException("Transaction not found with id: " + id);
                });
        log.info("Transaction fetched successfully with ID: {}", id);
        return mapToDTO(transaction);
    }

    @Override
    public TransactionDTO getTransactionByReference(String reference) {
        log.info("Fetching transaction by reference: {}", reference);
        Transaction transaction = transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> {
                    log.error("Transaction not found with reference: {}", reference);
                    return new ResourceNotFoundException("Transaction not found with reference: " + reference);
                });
        log.info("Transaction fetched successfully with reference: {}", reference);
        return mapToDTO(transaction);
    }

    @Override
    public List<TransactionDTO> getTransactionsByDebitCardId(Long debitCardId) {
        log.info("Fetching transactions by debit card ID: {}", debitCardId);
        if (!debitCardRepository.existsById(debitCardId)) {
            log.error("Debit card not found with ID: {}", debitCardId);
            throw new ResourceNotFoundException("Debit card not found with id: " + debitCardId);
        }
        List<Transaction> transactions = transactionRepository.findByDebitCardId(debitCardId);
        log.info("{} transactions found for debit card ID: {}", transactions.size(), debitCardId);
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByCreditCardId(Long creditCardId) {
        log.info("Fetching transactions by credit card ID: {}", creditCardId);
        if (!creditCardRepository.existsById(creditCardId)) {
            log.error("Credit card not found with ID: {}", creditCardId);
            throw new ResourceNotFoundException("Credit card not found with id: " + creditCardId);
        }
        List<Transaction> transactions = transactionRepository.findByCreditCardId(creditCardId);
        log.info("{} transactions found for credit card ID: {}", transactions.size(), creditCardId);
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        log.info("Fetching all transactions");
        List<Transaction> transactions = transactionRepository.findAll();
        log.info("{} transactions found", transactions.size());
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        log.info("Updating transaction with ID: {}", id);
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Transaction not found with ID: {}", id);
                    return new ResourceNotFoundException("Transaction not found with id: " + id);
                });

        transactionDTO.setId(existingTransaction.getId());
        Transaction transaction = prepareTransaction(transactionDTO);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transaction updated successfully with ID: {}", id);
        return mapToDTO(updatedTransaction);
    }
    @Override
    public void deleteTransaction(Long id) {
        log.info("Deleting transaction with ID: {}", id);
        if (!transactionRepository.existsById(id)) {
            log.error("Transaction not found with ID: {}", id);
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
        log.info("Transaction deleted successfully with ID: {}", id);
    }

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    private Transaction prepareTransaction(TransactionDTO transactionDTO) {
        if (transactionDTO.getDebitCardId() == null && transactionDTO.getCreditCardId() == null) {
            log.error("Either debit card or credit card must be provided");
            throw new IllegalArgumentException("Either debit card or credit card must be provided");
        }

        DebitCard debitCard = null;
        if (transactionDTO.getDebitCardId() != null) {
            log.info("Fetching debit card by ID: {}", transactionDTO.getDebitCardId());
            debitCard = debitCardRepository.findById(transactionDTO.getDebitCardId())
                    .orElseThrow(() -> {
                        log.error("Debit card not found with ID: {}", transactionDTO.getDebitCardId());
                        return new ResourceNotFoundException("Debit card not found with id: " + transactionDTO.getDebitCardId());
                    });
        }

        CreditCard creditCard = null;
        if (transactionDTO.getCreditCardId() != null) {
            log.info("Fetching credit card by ID: {}", transactionDTO.getCreditCardId());
            creditCard = creditCardRepository.findById(transactionDTO.getCreditCardId())
                    .orElseThrow(() -> {
                        log.error("Credit card not found with ID: {}", transactionDTO.getCreditCardId());
                        return new ResourceNotFoundException("Credit card not found with id: " + transactionDTO.getCreditCardId());
                    });
        }

        if (transactionDTO.getTransactionReference() != null &&
                transactionRepository.existsByTransactionReference(transactionDTO.getTransactionReference())) {
            log.error("Transaction reference already exists: {}", transactionDTO.getTransactionReference());
            throw new IllegalArgumentException("Transaction reference already exists");
        }

        Transaction transaction = mapToEntity(transactionDTO);
        transaction.setDebitCard(debitCard);
        transaction.setCreditCard(creditCard);
        return transaction;
    }
    private Transaction mapToEntity(TransactionDTO dto) {
        Transaction transaction = modelMapper.map(dto, Transaction.class);

        if (dto.getTransactionType() != null) {
            transaction.setTransactionType(TransactionType.valueOf(dto.getTransactionType()));
        }

        return transaction;
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = modelMapper.map(transaction, TransactionDTO.class);

        if (transaction.getDebitCard() != null) {
            dto.setDebitCardId(transaction.getDebitCard().getId());
        }

        if (transaction.getCreditCard() != null) {
            dto.setCreditCardId(transaction.getCreditCard().getId());
        }

        return dto;
    }

}
