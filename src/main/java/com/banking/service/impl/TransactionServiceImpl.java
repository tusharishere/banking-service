package com.banking.service.impl;

import com.banking.payload.TransactionDTO;
import com.banking.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        return null;
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        return null;
    }

    @Override
    public TransactionDTO getTransactionByReference(String reference) {
        return null;
    }

    @Override
    public List<TransactionDTO> getTransactionsByDebitCardId(Long debitCardId) {
        return List.of();
    }

    @Override
    public List<TransactionDTO> getTransactionsByCreditCardId(Long creditCardId) {
        return List.of();
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return List.of();
    }

    @Override
    public TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        return null;
    }

    @Override
    public void deleteTransaction(Long id) {

    }
}
