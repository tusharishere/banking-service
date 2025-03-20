package com.banking.service.impl;

import com.banking.entity.Account;
import com.banking.entity.DebitCard;
import com.banking.exception.ResourceNotFoundException;
import com.banking.payload.DebitCardDTO;
import com.banking.repository.AccountRepository;
import com.banking.repository.DebitCardRepository;
import com.banking.service.DebitCardService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        Account account = accountRepository.findById(debitCardDTO.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + debitCardDTO.getAccountId()));

        if (debitCardRepository.existsByCardNumber(debitCardDTO.getCardNumber())) {
            throw new IllegalArgumentException("Card number already exists");
        }

        if (debitCardRepository.existsByAccountId(debitCardDTO.getAccountId())) {
            throw new IllegalArgumentException("Account already has a debit card");
        }

        DebitCard debitCard = mapToEntity(debitCardDTO);
        debitCard.setAccount(account);
        DebitCard savedCard = debitCardRepository.save(debitCard);
        return mapToDTO(savedCard);

    }

    @Override
    public DebitCardDTO getDebitCardById(Long id) {
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debit card not found with id: " + id));
        return mapToDTO(debitCard);
    }

    @Override
    public DebitCardDTO getDebitCardByCardNumber(String cardNumber) {
        DebitCard debitCard = debitCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Debit card not found with number: " + cardNumber));
        return mapToDTO(debitCard);
    }

    @Override
    public DebitCardDTO getDebitCardByAccountId(Long accountId) {
        DebitCard debitCard = debitCardRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Debit card not found for account id: " + accountId));
        return mapToDTO(debitCard);
    }

    @Override
    public List<DebitCardDTO> getAllDebitCards() {
        List<DebitCard> debitCards = debitCardRepository.findAll();
        return debitCards.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DebitCardDTO updateDebitCard(Long id, DebitCardDTO debitCardDTO) {
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debit card not found with id: " + id));


        if (!debitCard.getCardNumber().equals(debitCardDTO.getCardNumber()) &&
                debitCardRepository.existsByCardNumber(debitCardDTO.getCardNumber())) {
            throw new IllegalArgumentException("Card number already exists");
        }

        debitCard.setExpiryDate(debitCardDTO.getExpiryDate());
        debitCard.setActive(debitCardDTO.isActive());

        DebitCard updatedCard = debitCardRepository.save(debitCard);
        return mapToDTO(updatedCard);
    }

    @Override
    @Transactional
    public void deleteDebitCard(Long id) {
        if (!debitCardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Debit card not found with id: " + id);
        }
        debitCardRepository.deleteById(id);
    }

    private DebitCardDTO mapToDTO(DebitCard debitCard) {
        return modelMapper.map(debitCard,DebitCardDTO.class);
    }

    private DebitCard mapToEntity(DebitCardDTO debitCardDTO) {
        return modelMapper.map(debitCardDTO,DebitCard.class);
    }
}
