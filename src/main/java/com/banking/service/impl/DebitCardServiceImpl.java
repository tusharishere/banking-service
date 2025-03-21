package com.banking.service.impl;

import com.banking.entity.Account;
import com.banking.entity.DebitCard;
import com.banking.exception.ResourceNotFoundException;
import com.banking.payload.DebitCardDTO;
import com.banking.repository.AccountRepository;
import com.banking.repository.DebitCardRepository;
import com.banking.service.DebitCardService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DebitCardServiceImpl implements DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public DebitCardServiceImpl(DebitCardRepository debitCardRepository, AccountRepository accountRepository, ModelMapper modelMapper) {
        this.debitCardRepository = debitCardRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public DebitCardDTO createDebitCard(DebitCardDTO debitCardDTO) {
        log.info("Creating debit card for account ID: {}", debitCardDTO.getAccountId());
        Account account = accountRepository.findById(debitCardDTO.getAccountId())
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", debitCardDTO.getAccountId());
                    return new ResourceNotFoundException("Account not found with id: " + debitCardDTO.getAccountId());
                });

        if (debitCardRepository.existsByCardNumber(debitCardDTO.getCardNumber())) {
            log.error("Debit card number already exists: {}", debitCardDTO.getCardNumber());
            throw new IllegalArgumentException("Card number already exists");
        }

        if (debitCardRepository.existsByAccountId(debitCardDTO.getAccountId())) {
            log.error("Account already has a debit card, ID: {}", debitCardDTO.getAccountId());
            throw new IllegalArgumentException("Account already has a debit card");
        }

        DebitCard debitCard = mapToEntity(debitCardDTO);
        debitCard.setAccount(account);
        DebitCard savedCard = debitCardRepository.save(debitCard);
        log.info("Debit card created successfully for account ID: {}", debitCardDTO.getAccountId());
        return mapToDTO(savedCard);
    }

    @Override
    public DebitCardDTO getDebitCardById(Long id) {
        log.info("Fetching debit card by ID: {}", id);
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Debit card not found with ID: {}", id);
                    return new ResourceNotFoundException("Debit card not found with id: " + id);
                });
        log.info("Debit card fetched successfully with ID: {}", id);
        return mapToDTO(debitCard);
    }

    @Override
    public DebitCardDTO getDebitCardByCardNumber(String cardNumber) {
        log.info("Fetching debit card by card number: {}", cardNumber);
        DebitCard debitCard = debitCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> {
                    log.error("Debit card not found with number: {}", cardNumber);
                    return new ResourceNotFoundException("Debit card not found with number: " + cardNumber);
                });
        log.info("Debit card fetched successfully with number: {}", cardNumber);
        return mapToDTO(debitCard);
    }

    @Override
    public DebitCardDTO getDebitCardByAccountId(Long accountId) {
        log.info("Fetching debit card by account ID: {}", accountId);
        DebitCard debitCard = debitCardRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.error("Debit card not found for account ID: {}", accountId);
                    return new ResourceNotFoundException("Debit card not found for account id: " + accountId);
                });
        log.info("Debit card fetched successfully for account ID: {}", accountId);
        return mapToDTO(debitCard);

    }

    @Override
    public List<DebitCardDTO> getAllDebitCards() {
        log.info("Fetching all debit cards");
        List<DebitCard> debitCards = debitCardRepository.findAll();
        log.info("Fetched {} debit cards", debitCards.size());
        return debitCards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DebitCardDTO updateDebitCard(Long id, DebitCardDTO debitCardDTO) {
        log.info("Updating debit card with ID: {}", id);
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Debit card not found with ID: {}", id);
                    return new ResourceNotFoundException("Debit card not found with id: " + id);
                });

//        if (!debitCard.getCardNumber().equals(debitCardDTO.getCardNumber()) &&
//                debitCardRepository.existsByCardNumber(debitCardDTO.getCardNumber())) {
//            log.error("Debit card number already exists: {}", debitCardDTO.getCardNumber());
//            throw new IllegalArgumentException("Card number already exists");
//        }

        debitCard.setExpiryDate(debitCardDTO.getExpiryDate());
        debitCard.setActive(debitCardDTO.isActive());
        DebitCard updatedCard = debitCardRepository.save(debitCard);
        log.info("Debit card updated successfully with ID: {}", id);
        return mapToDTO(updatedCard);
    }

    @Override
    @Transactional
    public void deleteDebitCard(Long id) {
        log.info("Deleting debit card with ID: {}", id);
        if (!debitCardRepository.existsById(id)) {
            log.error("Debit card not found with ID: {}", id);
            throw new ResourceNotFoundException("Debit card not found with id: " + id);
        }
        debitCardRepository.deleteById(id);
        log.info("Debit card deleted successfully with ID: {}", id);
    }

    private DebitCardDTO mapToDTO(DebitCard debitCard) {
        return modelMapper.map(debitCard,DebitCardDTO.class);
    }

    private DebitCard mapToEntity(DebitCardDTO debitCardDTO) {
        return modelMapper.map(debitCardDTO,DebitCard.class);
    }
}
