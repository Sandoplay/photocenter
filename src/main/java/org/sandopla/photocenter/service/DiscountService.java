package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.model.Product;
import org.sandopla.photocenter.repository.DiscountCardRepository;
import org.sandopla.photocenter.model.DiscountCard;
import org.sandopla.photocenter.repository.OrderRepository;
import org.sandopla.photocenter.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class DiscountService {
    private final DiscountCardRepository discountCardRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DiscountService(DiscountCardRepository discountCardRepository,
                           OrderRepository orderRepository,
                           ProductRepository productRepository) {
        this.discountCardRepository = discountCardRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // Розрахунок знижки за кількість фотографій
    public BigDecimal calculateQuantityDiscount(int photoCount) {
        // Приклад логіки знижок за кількість:
        if (photoCount >= 1000) return new BigDecimal("0.20"); // 20%
        if (photoCount >= 500) return new BigDecimal("0.15");  // 15%
        if (photoCount >= 100) return new BigDecimal("0.10");  // 10%
        if (photoCount >= 50) return new BigDecimal("0.05");   // 5%
        return BigDecimal.ZERO;
    }

    // Перевірка та застосування знижки дисконтної карти
    public Optional<BigDecimal> getDiscountCardPercentage(Client client) {
        return discountCardRepository.findActiveCardByClient(client)
                .map(DiscountCard::getDiscountPercentage);
    }

    // Розрахунок персональної знижки для професіонала
    public BigDecimal calculateProfessionalDiscount(Client client, Branch branch) {
        if (client.getType() != Client.ClientType.PROFESSIONAL) {
            return BigDecimal.ZERO;
        }

        // Отримуємо кількість замовлень клієнта в цій філії
        int orderCount = orderRepository.countByClientAndBranch(client, branch);

        // Приклад логіки персональних знижок:
        if (orderCount >= 50) return new BigDecimal("0.15");     // 15%
        if (orderCount >= 20) return new BigDecimal("0.10");     // 10%
        if (orderCount >= 10) return new BigDecimal("0.05");     // 5%
        return BigDecimal.ZERO;
    }

    // Перевірка можливості безкоштовної проявки плівки
    public boolean isFilmDevelopmentFree(Product film, Branch developmentBranch) {
        // Перевіряємо, чи була плівка куплена в цій філії
        return orderRepository.existsByProductAndBranch(film, developmentBranch);
    }

    // Загальний метод розрахунку знижки для замовлення
    public BigDecimal calculateTotalDiscount(Order order) {
        BigDecimal totalDiscount = BigDecimal.ZERO;
        Client client = order.getClient();
        Branch branch = order.getBranch();

        // 1. Знижка за кількість фотографій
        int totalPhotoCount = calculateTotalPhotoCount(order);
        totalDiscount = totalDiscount.max(calculateQuantityDiscount(totalPhotoCount));

        // 2. Знижка по дисконтній карті
        Optional<BigDecimal> cardDiscount = getDiscountCardPercentage(client);
        if (cardDiscount.isPresent()) {
            totalDiscount = totalDiscount.max(cardDiscount.get());
        }

        // 3. Персональна знижка для професіонала
        BigDecimal professionalDiscount = calculateProfessionalDiscount(client, branch);
        totalDiscount = totalDiscount.max(professionalDiscount);

        // Обмеження максимальної знижки
        return totalDiscount.min(new BigDecimal("0.30")); // Максимальна знижка 30%
    }

    // Допоміжний метод для підрахунку загальної кількості фотографій
    private int calculateTotalPhotoCount(Order order) {
        return order.getOrderDetails().stream()
                .mapToInt(detail -> {
                    if (detail.getService() != null &&
                            detail.getService().getName().contains("друк")) {
                        return detail.getQuantity();
                    }
                    return 0;
                })
                .sum();
    }
}