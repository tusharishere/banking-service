package com.banking.repository;

import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.entity.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
  List<Account> findByCustomerId(Long customerId);
  Optional<Account> findByAccountNumber(String accountNumber);
  boolean existsByCustomerAndAccountType(Customer customer, AccountType accountType);
  boolean existsByAccountNumber(String accountNumber);
}