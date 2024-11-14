package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.OrderDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PriceCalculationService {
    public BigDecimal calculateDetailPrice(OrderDetail detail, BigDecimal volumeDiscount, boolean hasDiscountCard) {
        BigDecimal totalPrice = detail.getPrice()
                .multiply(BigDecimal.valueOf(detail.getQuantity()));

        if (detail.getService() != null && volumeDiscount != null &&
                volumeDiscount.compareTo(BigDecimal.ZERO) > 0) {
            // Знижка за кількість для послуг
            BigDecimal discount = totalPrice.multiply(volumeDiscount);
            totalPrice = totalPrice.subtract(discount);
        } else if (detail.getProduct() != null && hasDiscountCard) {
            // Знижка по дисконтній картці для товарів
            BigDecimal discount = totalPrice.multiply(new BigDecimal("0.10"));
            totalPrice = totalPrice.subtract(discount);
        }

        return totalPrice;
    }

    public BigDecimal calculateDetailPrice(OrderDetail detail, BigDecimal volumeDiscount) {
        BigDecimal totalPrice = detail.getPrice()
                .multiply(BigDecimal.valueOf(detail.getQuantity()));

        if (detail.getService() != null && volumeDiscount != null &&
                volumeDiscount.compareTo(BigDecimal.ZERO) > 0) {
            // Знижка за кількість для послуг
            BigDecimal discount = totalPrice.multiply(volumeDiscount);
            totalPrice = totalPrice.subtract(discount);
        }

        return totalPrice;
    }
}