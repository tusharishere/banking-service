package com.banking.controller;

import com.banking.payload.TransactionDTO;
import com.banking.service.impl.TransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("add-transaction")
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO createdTransaction = transactionService.createTransaction(transactionDTO);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        TransactionDTO transactionDTO = transactionService.getTransactionById(id);
        return new ResponseEntity<>(transactionDTO, HttpStatus.OK);
    }

    @GetMapping("/debit-card/{debitCardId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDebitCardId(@PathVariable Long debitCardId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByDebitCardId(debitCardId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/credit-card/{creditCardId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCreditCardId(@PathVariable Long creditCardId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByCreditCardId(creditCardId);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<TransactionDTO> getTransactionByReference(@PathVariable String reference) {
        TransactionDTO transactionDTO = transactionService.getTransactionByReference(reference);
        return new ResponseEntity<>(transactionDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO updatedTransaction = transactionService.updateTransaction(id, transactionDTO);
        return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
    }



}
