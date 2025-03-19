package com.banking.service.impl;

import com.banking.payload.CreditCardDTO;
import com.banking.service.CreditCardService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CreditCardServiceImpl implements CreditCardService {
    @Override
    public CreditCardDTO createCreditCard(CreditCardDTO creditCardDTO) {
        return null;
    }

    @Override
    public CreditCardDTO getCreditCardById(Long id) {
        return null;
    }

    @Override
    public CreditCardDTO getCreditCardByCardNumber(String cardNumber) {
        return null;
    }

    @Override
    public CreditCardDTO getCreditCardByAccountId(Long accountId) {
        return null;
    }

    @Override
    public List<CreditCardDTO> getAllCreditCards() {
        return List.of();
    }

    @Override
    public CreditCardDTO updateCreditCard(Long id, CreditCardDTO creditCardDTO) {
        return null;
    }

    @Override
    public void deleteCreditCard(Long id) {

    }
}
