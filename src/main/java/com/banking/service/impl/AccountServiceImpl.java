package com.banking.service.impl;

import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.exception.ResourceNotFoundException;
import com.banking.payload.AccountDTO;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.service.AccountService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {

        Customer customer = customerRepository.findById(accountDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + accountDTO.getCustomerId()));


        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists");
        }

        if (accountRepository.existsByCustomerAndAccountType(customer, accountDTO.getAccountType())) {
            throw new IllegalArgumentException("Customer already has an account of type " + accountDTO.getAccountType());
        }

        Account account = mapToEntity(accountDTO);
        account.setCustomer(customer);
        Account savedAccount = accountRepository.save(account);
        return mapToDTO(savedAccount);
    }

    @Override
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return mapToDTO(account);
    }

    @Override
    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        return accounts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public AccountDTO getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        return mapToDTO(account);
    }

    @Override
    public List<AccountDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        if (!account.getAccountNumber().equals(accountDTO.getAccountNumber()) &&
                accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists");
        }
        account.setAccountType(accountDTO.getAccountType());
        Account updatedAccount = accountRepository.save(account);
        return mapToDTO(updatedAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    public AccountDTO mapToDTO(Account account) {
        AccountDTO dto = modelMapper.map(account, AccountDTO.class);
        dto.setCustomerId(account.getCustomer().getId());
        return dto;
    }

    public Account mapToEntity(AccountDTO dto) {
        Account account = modelMapper.map(dto, Account.class);
        return account;
    }


}
