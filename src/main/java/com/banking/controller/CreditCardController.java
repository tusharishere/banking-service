package com.banking.controller;

import com.banking.payload.CreditCardDTO;
import com.banking.service.impl.CreditCardServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/credit-cards")
public class CreditCardController {

    private final CreditCardServiceImpl creditCardService;

    public CreditCardController(CreditCardServiceImpl creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping("/add-credit-card")
    public ResponseEntity<CreditCardDTO> createCreditCard(@Valid @RequestBody CreditCardDTO creditCardDTO) {
        CreditCardDTO createdCard = creditCardService.createCreditCard(creditCardDTO);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditCardDTO> getCreditCardById(@PathVariable Long id) {
        CreditCardDTO creditCardDTO = creditCardService.getCreditCardById(id);
        return new ResponseEntity<>(creditCardDTO, HttpStatus.OK);
    }

    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<CreditCardDTO> getCreditCardByCardNumber(@PathVariable String cardNumber) {
        CreditCardDTO creditCardDTO = creditCardService.getCreditCardByCardNumber(cardNumber);
        return new ResponseEntity<>(creditCardDTO, HttpStatus.OK);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<CreditCardDTO> getCreditCardByAccountId(@PathVariable Long accountId) {
        CreditCardDTO creditCardDTO = creditCardService.getCreditCardByAccountId(accountId);
        return new ResponseEntity<>(creditCardDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CreditCardDTO>> getAllCreditCards() {
        List<CreditCardDTO> creditCards = creditCardService.getAllCreditCards();
        return new ResponseEntity<>(creditCards, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditCardDTO> updateCreditCard(@PathVariable Long id,
                                                          @Valid @RequestBody CreditCardDTO creditCardDTO) {
        CreditCardDTO updatedCard = creditCardService.updateCreditCard(id, creditCardDTO);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCreditCard(@PathVariable Long id) {
        creditCardService.deleteCreditCard(id);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
    }
}
