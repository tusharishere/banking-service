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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        if (transactionDTO.getDebitCardId() == null && transactionDTO.getCreditCardId() == null) {
            throw new IllegalArgumentException("Either debit card or credit card must be provided");
        }

        DebitCard debitCard = null;
        if (transactionDTO.getDebitCardId() != null) {
            debitCard = debitCardRepository.findById(transactionDTO.getDebitCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Debit card not found with id: " + transactionDTO.getDebitCardId()));
        }

        CreditCard creditCard = null;
        if (transactionDTO.getCreditCardId() != null) {
            creditCard = creditCardRepository.findById(transactionDTO.getCreditCardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Credit card not found with id: " + transactionDTO.getCreditCardId()));
        }

        if (transactionDTO.getTransactionReference() != null &&
                transactionRepository.existsByTransactionReference(transactionDTO.getTransactionReference())) {
            throw new IllegalArgumentException("Transaction reference already exists");
        }


        if (transactionDTO.getTransactionReference() == null || transactionDTO.getTransactionReference().isEmpty()) {
            transactionDTO.setTransactionReference(generateTransactionReference());
        }

        if (transactionDTO.getTransactionTimestamp() == null) {
            transactionDTO.setTransactionTimestamp(LocalDateTime.now());
        }

        Transaction transaction = mapToEntity(transactionDTO);
        transaction.setDebitCard(debitCard);
        transaction.setCreditCard(creditCard);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToDTO(savedTransaction);
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return mapToDTO(transaction);
    }

    @Override
    public TransactionDTO getTransactionByReference(String reference) {
        Transaction transaction = transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference: " + reference));
        return mapToDTO(transaction);
    }

    @Override
    public List<TransactionDTO> getTransactionsByDebitCardId(Long debitCardId) {
        if (!debitCardRepository.existsById(debitCardId)) {
            throw new ResourceNotFoundException("Debit card not found with id: " + debitCardId);
        }

        List<Transaction> transactions = transactionRepository.findByDebitCardId(debitCardId);
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByCreditCardId(Long creditCardId) {
        if (!creditCardRepository.existsById(creditCardId)) {
            throw new ResourceNotFoundException("Credit card not found with id: " + creditCardId);
        }

        List<Transaction> transactions = transactionRepository.findByCreditCardId(creditCardId);
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (transactionDTO.getTransactionReference() != null &&
                !transaction.getTransactionReference().equals(transactionDTO.getTransactionReference()) &&
                transactionRepository.existsByTransactionReference(transactionDTO.getTransactionReference())) {
            throw new IllegalArgumentException("Transaction reference already exists");
        }

        transaction.setTransactionReference(transactionDTO.getTransactionReference());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTransactionType(TransactionType.valueOf(transactionDTO.getTransactionType()));
        transaction.setTransactionTimestamp(transactionDTO.getTransactionTimestamp());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return mapToDTO(updatedTransaction);

    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Transaction mapToEntity(TransactionDTO dto){
        return modelMapper.map(dto,Transaction.class);
    }

    private TransactionDTO mapToDTO(Transaction transaction){
        return modelMapper.map(transaction,TransactionDTO.class);
    }
}
