package com.banking.service.impl;

import com.banking.entity.Account;
import com.banking.entity.CreditCard;
import com.banking.exception.ResourceNotFoundException;
import com.banking.payload.CreditCardDTO;
import com.banking.repository.AccountRepository;
import com.banking.repository.CreditCardRepository;
import com.banking.service.CreditCardService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public CreditCardServiceImpl(CreditCardRepository creditCardRepository, AccountRepository accountRepository, ModelMapper modelMapper) {
        this.creditCardRepository = creditCardRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CreditCardDTO createCreditCard(CreditCardDTO creditCardDTO) {
        log.info("Creating credit card with number: {}", creditCardDTO.getCardNumber());
        Account account = accountRepository.findById(creditCardDTO.getAccountId())
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", creditCardDTO.getAccountId());
                    return new ResourceNotFoundException("Account not found with id: " + creditCardDTO.getAccountId());
                });

        if (creditCardRepository.existsByCardNumber(creditCardDTO.getCardNumber())) {
            log.warn("Card number already exists: {}", creditCardDTO.getCardNumber());
            throw new IllegalArgumentException("Card number already exists");
        }

        if (creditCardRepository.existsByAccountId(creditCardDTO.getAccountId())) {
            log.warn("Account already has a credit card with ID: {}", creditCardDTO.getAccountId());
            throw new IllegalArgumentException("Account already has a credit card");
        }

        CreditCard creditCard = mapToEntity(creditCardDTO);
        creditCard.setAccount(account);
        CreditCard savedCard = creditCardRepository.save(creditCard);
        log.info("Credit card created successfully with ID: {}", savedCard.getId());
        return mapToDTO(savedCard);
    }

    @Override
    public CreditCardDTO getCreditCardById(Long id) {
        log.info("Fetching credit card by ID: {}", id);
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Credit card not found with ID: {}", id);
                    return new ResourceNotFoundException("Credit card not found with id: " + id);
                });
        log.info("Credit card fetched successfully with ID: {}", id);
        return mapToDTO(creditCard);
    }

    @Override
    public CreditCardDTO getCreditCardByCardNumber(String cardNumber) {
        log.info("Fetching credit card by card number: {}", cardNumber);
        CreditCard creditCard = creditCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> {
                    log.error("Credit card not found with number: {}", cardNumber);
                    return new ResourceNotFoundException("Credit card not found with number: " + cardNumber);
                });
        log.info("Credit card fetched successfully with number: {}", cardNumber);
        return mapToDTO(creditCard);
    }

    @Override
    public CreditCardDTO getCreditCardByAccountId(Long accountId) {
        log.info("Fetching credit card by account ID: {}", accountId);
        CreditCard creditCard = creditCardRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.error("Credit card not found for account ID: {}", accountId);
                    return new ResourceNotFoundException("Credit card not found for account ID: " + accountId);
                });
        log.info("Credit card fetched successfully for account ID: {}", accountId);
        return mapToDTO(creditCard);
    }

    @Override
    public List<CreditCardDTO> getAllCreditCards() {
        log.info("Fetching all credit cards");
        List<CreditCard> creditCards = creditCardRepository.findAll();
        log.info("Total credit cards fetched: {}", creditCards.size());
        return creditCards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CreditCardDTO updateCreditCard(Long id, CreditCardDTO creditCardDTO) {
        log.info("Updating credit card with ID: {}", id);
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Credit card not found with ID: {}", id);
                    return new ResourceNotFoundException("Credit card not found with id: " + id);
                });

        if (!creditCard.getCardNumber().equals(creditCardDTO.getCardNumber()) &&
                creditCardRepository.existsByCardNumber(creditCardDTO.getCardNumber())) {
            log.warn("Card number already exists: {}", creditCardDTO.getCardNumber());
            throw new IllegalArgumentException("Card number already exists");
        }

        creditCard.setCardNumber(creditCardDTO.getCardNumber());
        creditCard.setExpiryDate(creditCardDTO.getExpiryDate());
        creditCard.setCvv(creditCardDTO.getCvv());
        creditCard.setActive(creditCardDTO.isActive());
        creditCard.setCreditLimit(creditCardDTO.getCreditLimit());
        creditCard.setAvailableCredit(creditCardDTO.getAvailableCredit());

        CreditCard updatedCard = creditCardRepository.save(creditCard);
        log.info("Credit card updated successfully with ID: {}", id);
        return mapToDTO(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCreditCard(Long id) {
        log.info("Deleting credit card with ID: {}", id);
        if (!creditCardRepository.existsById(id)) {
            log.error("Credit card not found with ID: {}", id);
            throw new ResourceNotFoundException("Credit card not found with id: " + id);
        }
        creditCardRepository.deleteById(id);
        log.info("Credit card deleted successfully with ID: {}", id);
    }

    private CreditCardDTO mapToDTO (CreditCard creditCard){
        return modelMapper.map(creditCard,CreditCardDTO.class);
    }

    private CreditCard mapToEntity (CreditCardDTO dto){
        return modelMapper.map(dto,CreditCard.class);
    }
}
