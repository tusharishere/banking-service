package com.banking.service;

import com.banking.payload.DebitCardDTO;

import java.util.List;

public interface DebitCardService {
    DebitCardDTO createDebitCard(DebitCardDTO debitCardDTO);
    DebitCardDTO getDebitCardById(Long id);
    DebitCardDTO getDebitCardByCardNumber(String cardNumber);
    DebitCardDTO getDebitCardByAccountId(Long accountId);
    List<DebitCardDTO> getAllDebitCards();
    DebitCardDTO updateDebitCard(Long id, DebitCardDTO debitCardDTO);
    void deleteDebitCard(Long id);
}
