package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.DiscountCard;
import org.sandopla.photocenter.service.DiscountCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discount-cards")
public class DiscountCardController {

    private final DiscountCardService discountCardService;

    @Autowired
    public DiscountCardController(DiscountCardService discountCardService) {
        this.discountCardService = discountCardService;
    }

    @GetMapping
    public List<DiscountCard> getAllDiscountCards() {
        return discountCardService.getAllDiscountCards();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountCard> getDiscountCardById(@PathVariable Long id) {
        DiscountCard discountCard = discountCardService.getDiscountCardById(id);
        return ResponseEntity.ok(discountCard);
    }

    @PostMapping
    public ResponseEntity<DiscountCard> createDiscountCard(@RequestBody DiscountCard discountCard) {
        DiscountCard newDiscountCard = discountCardService.createDiscountCard(discountCard);
        return ResponseEntity.ok(newDiscountCard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountCard> updateDiscountCard(@PathVariable Long id, @RequestBody DiscountCard discountCardDetails) {
        DiscountCard updatedDiscountCard = discountCardService.updateDiscountCard(id, discountCardDetails);
        return ResponseEntity.ok(updatedDiscountCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscountCard(@PathVariable Long id) {
        discountCardService.deleteDiscountCard(id);
        return ResponseEntity.ok().build();
    }
}
