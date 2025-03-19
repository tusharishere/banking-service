package com.banking.service;

import com.banking.payload.TransactionDTO;

import java.util.List;

public interface TransactionService {
    TransactionDTO createTransaction(TransactionDTO transactionDTO);
    TransactionDTO getTransactionById(Long id);
    TransactionDTO getTransactionByReference(String reference);
    List<TransactionDTO> getTransactionsByDebitCardId(Long debitCardId);
    List<TransactionDTO> getTransactionsByCreditCardId(Long creditCardId);
    List<TransactionDTO> getAllTransactions();
    TransactionDTO updateTransaction(Long id, TransactionDTO transactionDTO);
    void deleteTransaction(Long id);
}
