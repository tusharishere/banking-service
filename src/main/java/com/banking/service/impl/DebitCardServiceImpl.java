package com.banking.service.impl;

import com.banking.payload.DebitCardDTO;
import com.banking.service.DebitCardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebitCardServiceImpl implements DebitCardService {
    @Override
    public DebitCardDTO createDebitCard(DebitCardDTO debitCardDTO) {
        return null;
    }

    @Override
    public DebitCardDTO getDebitCardById(Long id) {
        return null;
    }

    @Override
    public DebitCardDTO getDebitCardByCardNumber(String cardNumber) {
        return null;
    }

    @Override
    public DebitCardDTO getDebitCardByAccountId(Long accountId) {
        return null;
    }

    @Override
    public List<DebitCardDTO> getAllDebitCards() {
        return List.of();
    }

    @Override
    public DebitCardDTO updateDebitCard(Long id, DebitCardDTO debitCardDTO) {
        return null;
    }

    @Override
    public void deleteDebitCard(Long id) {

    }
}
