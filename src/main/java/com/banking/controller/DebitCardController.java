package com.banking.controller;

import com.banking.payload.DebitCardDTO;
import com.banking.service.impl.DebitCardServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/debit-cards")
public class DebitCardController {

    private final DebitCardServiceImpl debitCardService;

    public DebitCardController(DebitCardServiceImpl debitCardService) {
        this.debitCardService = debitCardService;
    }

    @PostMapping("add-debit-card")
    public ResponseEntity<DebitCardDTO> createDebitCard(@Valid @RequestBody DebitCardDTO debitCardDTO) {
        DebitCardDTO createdCard = debitCardService.createDebitCard(debitCardDTO);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebitCardDTO> getDebitCardById(@PathVariable Long id) {
        DebitCardDTO debitCardDTO = debitCardService.getDebitCardById(id);
        return new ResponseEntity<>(debitCardDTO, HttpStatus.OK);
    }

    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<DebitCardDTO> getDebitCardByCardNumber(@PathVariable String cardNumber) {
        DebitCardDTO debitCardDTO = debitCardService.getDebitCardByCardNumber(cardNumber);
        return new ResponseEntity<>(debitCardDTO,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DebitCardDTO>> getAllDebitCards() {
        List<DebitCardDTO> debitCards = debitCardService.getAllDebitCards();
        return new ResponseEntity<>(debitCards,HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DebitCardDTO> updateDebitCard(@PathVariable Long id,
                                                        @Valid @RequestBody DebitCardDTO debitCardDTO) {
        DebitCardDTO updatedCard = debitCardService.updateDebitCard(id, debitCardDTO);
        return new ResponseEntity<>(updatedCard,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDebitCard(@PathVariable Long id) {
        debitCardService.deleteDebitCard(id);
        return new ResponseEntity<>("Deleted Successfully",HttpStatus.OK);
    }
}
