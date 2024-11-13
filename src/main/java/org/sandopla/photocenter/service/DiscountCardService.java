package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.DiscountCard;
import org.sandopla.photocenter.repository.DiscountCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class DiscountCardService {
    private final DiscountCardRepository discountCardRepository;
    private final Random random = new Random();

    @Autowired
    public DiscountCardService(DiscountCardRepository discountCardRepository) {
        this.discountCardRepository = discountCardRepository;
    }

    // Створення нової дисконтної карти
    public DiscountCard createDiscountCard(Client client) {
        // Перевіряємо чи немає вже активної карти
        if (discountCardRepository.findByClientAndActive(client, true).isPresent()) {
            throw new IllegalStateException("Client already has active discount card");
        }

        DiscountCard card = new DiscountCard();
        card.setClient(client);
        card.setCardNumber(generateCardNumber());
        card.setDiscountPercentage(new BigDecimal("0.10")); // Базова знижка 10%
        card.setIssueDate(LocalDate.now());
        card.setExpiryDate(LocalDate.now().plusYears(1)); // Карта дійсна 1 рік
        card.setActive(true);

        return discountCardRepository.save(card);
    }

    public List<DiscountCard> getClientCards(Client client) {
        return discountCardRepository.findByClient(client);
    }


    // Генерація унікального номера карти
    private String generateCardNumber() {
        String number;
        do {
            // Генеруємо 8-значний номер
            number = String.format("%08d", random.nextInt(100000000));
        } while (discountCardRepository.existsByCardNumber(number));
        return number;
    }

    // Деактивація карти
    public void deactivateCard(String cardNumber) {
        DiscountCard card = discountCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        card.setActive(false);
        discountCardRepository.save(card);
    }

    // Подовження терміну дії карти
    public DiscountCard extendValidity(String cardNumber, int months) {
        DiscountCard card = discountCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        LocalDate newExpiryDate = card.getExpiryDate() != null ?
                card.getExpiryDate().plusMonths(months) :
                LocalDate.now().plusMonths(months);

        card.setExpiryDate(newExpiryDate);
        return discountCardRepository.save(card);
    }

    // Зміна відсотка знижки
    public DiscountCard updateDiscountPercentage(String cardNumber, BigDecimal newPercentage) {
        if (newPercentage.compareTo(BigDecimal.ZERO) < 0 || newPercentage.compareTo(new BigDecimal("0.50")) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 50%");
        }

        DiscountCard card = discountCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setDiscountPercentage(newPercentage);
        return discountCardRepository.save(card);
    }

    // Перевірка чи карта активна і дійсна
    public boolean isCardValid(String cardNumber) {
        return discountCardRepository.findByCardNumber(cardNumber)
                .map(card -> card.isActive() &&
                        (card.getExpiryDate() == null || card.getExpiryDate().isAfter(LocalDate.now())))
                .orElse(false);
    }

    // Отримання активної знижки клієнта
    public Optional<BigDecimal> getClientDiscount(Client client) {
        return discountCardRepository.findByClientAndActive(client, true)
                .filter(card -> card.getExpiryDate() == null ||
                        card.getExpiryDate().isAfter(LocalDate.now()))
                .map(DiscountCard::getDiscountPercentage);
    }
}