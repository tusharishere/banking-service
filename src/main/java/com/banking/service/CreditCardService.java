package com.banking.service;

import com.banking.payload.CreditCardDTO;

import java.util.List;

public interface CreditCardService {
    CreditCardDTO createCreditCard(CreditCardDTO creditCardDTO);
    CreditCardDTO getCreditCardById(Long id);
    CreditCardDTO getCreditCardByCardNumber(String cardNumber);
    CreditCardDTO getCreditCardByAccountId(Long accountId);
    List<CreditCardDTO> getAllCreditCards();
    CreditCardDTO updateCreditCard(Long id, CreditCardDTO creditCardDTO);
    void deleteCreditCard(Long id);
}
