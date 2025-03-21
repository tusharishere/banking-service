package com.banking.service.impl;

import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.exception.ResourceNotFoundException;
import com.banking.payload.AccountDTO;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Creating account with number: {}", accountDTO.getAccountNumber());
        Customer customer = customerRepository.findById(accountDTO.getCustomerId())
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", accountDTO.getCustomerId());
                    return new ResourceNotFoundException("Customer not found with id: " + accountDTO.getCustomerId());
                });
        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            log.warn("Account number already exists: {}", accountDTO.getAccountNumber());
            throw new IllegalArgumentException("Account number already exists");
        }

        if (accountRepository.existsByCustomerAndAccountType(customer, accountDTO.getAccountType())) {
            log.warn("Customer already has an account of type: {}", accountDTO.getAccountType());
            throw new IllegalArgumentException("Customer already has an account of type " + accountDTO.getAccountType());
        }

        Account account = mapToEntity(accountDTO);
        account.setCustomer(customer);
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", savedAccount.getId());
        return mapToDTO(savedAccount);
    }

    @Override
    public AccountDTO getAccountById(Long id) {
        log.info("Fetching account by ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", id);
                    return new ResourceNotFoundException("Account not found with id: " + id);
                });
        log.info("Account fetched successfully with ID: {}", id);
        return mapToDTO(account);
    }

    @Override
    public List<AccountDTO> getAccountsByCustomerId(Long customerId) {
        log.info("Fetching accounts by customer ID: {}", customerId);

        if (!customerRepository.existsById(customerId)) {
            log.error("Customer not found with ID: {}", customerId);
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        log.info("Total accounts fetched for customer ID {}: {}", customerId, accounts.size());
        return accounts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public AccountDTO getAccountByAccountNumber(String accountNumber) {
        log.info("Fetching account by number: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found with number: {}", accountNumber);
                    return new ResourceNotFoundException("Account not found with number: " + accountNumber);
                });
        log.info("Account fetched successfully with number: {}", accountNumber);
        return mapToDTO(account);
    }

    @Override
    public List<AccountDTO> getAllAccounts() {
        log.info("Fetching all accounts");
        List<Account> accounts = accountRepository.findAll();
        log.info("Total accounts fetched: {}", accounts.size());
        return accounts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        log.info("Updating account with ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", id);
                    return new ResourceNotFoundException("Account not found with id: " + id);
                });

//        if (!account.getAccountNumber().equals(accountDTO.getAccountNumber()) &&
//                accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
//            log.warn("Account number already exists: {}", accountDTO.getAccountNumber());
//            throw new IllegalArgumentException("Account number already exists");
//        }

        account.setAccountType(accountDTO.getAccountType());
        Account updatedAccount = accountRepository.save(account);
        log.info("Account updated successfully with ID: {}", id);
        return mapToDTO(updatedAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        log.info("Deleting account with ID: {}", id);
        if (!accountRepository.existsById(id)) {
            log.error("Account not found with ID: {}", id);
            throw new ResourceNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
        log.info("Account deleted successfully with ID: {}", id);
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
