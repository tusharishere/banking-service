package com.banking.service.impl;

import com.banking.entity.Account;
import com.banking.entity.CreditCard;
import com.banking.exception.ResourceNotFoundException;
import com.banking.payload.CreditCardDTO;
import com.banking.repository.AccountRepository;
import com.banking.repository.CreditCardRepository;
import com.banking.service.CreditCardService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

        Account account = accountRepository.findById(creditCardDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + creditCardDTO.getAccountId()));

        if (creditCardRepository.existsByCardNumber(creditCardDTO.getCardNumber())) {
            throw new IllegalArgumentException("Card number already exists");
        }

        if (creditCardRepository.existsByAccountId(creditCardDTO.getAccountId())) {
            throw new IllegalArgumentException("Account already has a credit card");
        }

        CreditCard creditCard = mapToEntity(creditCardDTO);
        creditCard.setAccount(account);
        CreditCard savedCard = creditCardRepository.save(creditCard);
        return mapToDTO(savedCard);

    }

    @Override
    public CreditCardDTO getCreditCardById(Long id) {
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found with id: " + id));
        return mapToDTO(creditCard);
    }

    @Override
    public CreditCardDTO getCreditCardByCardNumber(String cardNumber) {
        CreditCard creditCard = creditCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found with number: " + cardNumber));
        return mapToDTO(creditCard);
    }

    @Override
    public CreditCardDTO getCreditCardByAccountId(Long accountId) {
        CreditCard creditCard = creditCardRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found for account id: " + accountId));
        return mapToDTO(creditCard);
    }

    @Override
    public List<CreditCardDTO> getAllCreditCards() {
        List<CreditCard> creditCards = creditCardRepository.findAll();
        return creditCards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CreditCardDTO updateCreditCard(Long id, CreditCardDTO creditCardDTO) {
        CreditCard creditCard = creditCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credit card not found with id: " + id));


        if (!creditCard.getCardNumber().equals(creditCardDTO.getCardNumber()) &&
                creditCardRepository.existsByCardNumber(creditCardDTO.getCardNumber())) {
            throw new IllegalArgumentException("Card number already exists");
        }

        creditCard.setCardNumber(creditCardDTO.getCardNumber());
        creditCard.setExpiryDate(creditCardDTO.getExpiryDate());
        creditCard.setCvv(creditCardDTO.getCvv());
        creditCard.setActive(creditCardDTO.isActive());
        creditCard.setCreditLimit(creditCardDTO.getCreditLimit());
        creditCard.setAvailableCredit(creditCardDTO.getAvailableCredit());

        CreditCard updatedCard = creditCardRepository.save(creditCard);
        return mapToDTO(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCreditCard(Long id) {
        if (!creditCardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Credit card not found with id: " + id);
        }
        creditCardRepository.deleteById(id);
    }

    private CreditCardDTO mapToDTO (CreditCard creditCard){
        return modelMapper.map(creditCard,CreditCardDTO.class);
    }

    private CreditCard mapToEntity (CreditCardDTO dto){
        return modelMapper.map(dto,CreditCard.class);
    }
}
