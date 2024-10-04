package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.DiscountCard;
import org.sandopla.photocenter.repository.DiscountCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountCardService {

    private final DiscountCardRepository discountCardRepository;

    @Autowired
    public DiscountCardService(DiscountCardRepository discountCardRepository) {
        this.discountCardRepository = discountCardRepository;
    }

    public List<DiscountCard> getAllDiscountCards() {
        return discountCardRepository.findAll();
    }

    public DiscountCard getDiscountCardById(Long id) {
        return discountCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DiscountCard not found with id: " + id));
    }

    public DiscountCard createDiscountCard(DiscountCard discountCard) {
        return discountCardRepository.save(discountCard);
    }

    public DiscountCard updateDiscountCard(Long id, DiscountCard discountCardDetails) {
        DiscountCard discountCard = getDiscountCardById(id);
        discountCard.setClient(discountCardDetails.getClient());
        discountCard.setDiscountPercentage(discountCardDetails.getDiscountPercentage());
        discountCard.setIssueDate(discountCardDetails.getIssueDate());
        discountCard.setExpirationDate(discountCardDetails.getExpirationDate());
        return discountCardRepository.save(discountCard);
    }

    public void deleteDiscountCard(Long id) {
        discountCardRepository.deleteById(id);
    }
}
