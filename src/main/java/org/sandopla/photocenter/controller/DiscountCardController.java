package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.DiscountCard;
import org.sandopla.photocenter.service.ClientService;
import org.sandopla.photocenter.service.DiscountCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/discount-cards")
public class DiscountCardController {

    private final DiscountCardService discountCardService;
    private final ClientService clientService;



    public DiscountCardController(DiscountCardService discountCardService, ClientService clientService) {
        this.discountCardService = discountCardService;
        this.clientService = clientService;

    }

    @PostMapping("/clients/{clientId}")
    public ResponseEntity<DiscountCard> createCard(@PathVariable Long clientId) {
        try {
            DiscountCard card = discountCardService.createDiscountCard(getClient(clientId));
            return ResponseEntity.ok(card);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{cardNumber}/deactivate")
    public ResponseEntity<Void> deactivateCard(@PathVariable String cardNumber) {
        try {
            discountCardService.deactivateCard(cardNumber);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{cardNumber}/extend")
    public ResponseEntity<DiscountCard> extendCard(
            @PathVariable String cardNumber,
            @RequestParam int months) {
        try {
            DiscountCard card = discountCardService.extendValidity(cardNumber, months);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{cardNumber}/discount")
    public ResponseEntity<DiscountCard> updateDiscount(
            @PathVariable String cardNumber,
            @RequestParam BigDecimal percentage) {
        try {
            DiscountCard card = discountCardService.updateDiscountPercentage(cardNumber, percentage);
            return ResponseEntity.ok(card);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{cardNumber}/valid")
    public ResponseEntity<Boolean> checkValidity(@PathVariable String cardNumber) {
        boolean isValid = discountCardService.isCardValid(cardNumber);
        return ResponseEntity.ok(isValid);
    }

    // Допоміжний метод для отримання клієнта
    private Client getClient(Long clientId) {
        // TODO: Implement client retrieval
        return clientService.getClientById(clientId);
    }
}