package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.OrderDetail;
import org.sandopla.photocenter.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderDetailService {

    private static final BigDecimal URGENT_PRICE_MULTIPLIER = new BigDecimal("1.10"); // +10%

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        // Перевіряємо і встановлюємо базову ціну
        if (orderDetail.getPrice() == null) {
            if (orderDetail.getProduct() != null) {
                orderDetail.setPrice(orderDetail.getProduct().getPrice());
            } else if (orderDetail.getService() != null) {
                // Для послуг використовуємо множник термінового замовлення з моделі Service
                BigDecimal servicePrice = orderDetail.getService().getBasePrice();
                if (orderDetail.getOrder().isUrgent()) {
                    servicePrice = servicePrice.multiply(orderDetail.getService().getUrgentPriceMultiplier());
                }
                orderDetail.setPrice(servicePrice);
            } else {
                throw new IllegalArgumentException("Either product or service must be specified");
            }
        }

        if (orderDetail.getQuantity() == null || orderDetail.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // Розраховуємо фінальну ціну
        BigDecimal finalPrice = orderDetail.getPrice()
                .multiply(BigDecimal.valueOf(orderDetail.getQuantity()));

        // Якщо замовлення термінове і це товар (не послуга), додаємо 10%
        if (orderDetail.getOrder().isUrgent() && orderDetail.getProduct() != null) {
            finalPrice = finalPrice.multiply(URGENT_PRICE_MULTIPLIER);
        }

        // Округляємо до 2 знаків після коми
        finalPrice = finalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);

        orderDetail.setPrice(finalPrice);

        return orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}