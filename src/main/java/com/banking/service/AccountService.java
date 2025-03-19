package com.banking.service;

import com.banking.payload.AccountDTO;

import java.util.List;

public interface AccountService {
    AccountDTO createAccount(AccountDTO accountDTO);
    AccountDTO getAccountById(Long id);
    List<AccountDTO> getAccountsByCustomerId(Long customerId);
    AccountDTO getAccountByAccountNumber(String accountNumber);
    List<AccountDTO> getAllAccounts();
    AccountDTO updateAccount(Long id, AccountDTO accountDTO);
    void deleteAccount(Long id);
}
