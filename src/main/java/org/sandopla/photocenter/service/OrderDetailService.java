package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.OrderDetail;
import org.sandopla.photocenter.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        // Перевіряємо і встановлюємо price
        if (orderDetail.getPrice() == null) {
            if (orderDetail.getProduct() != null) {
                orderDetail.setPrice(orderDetail.getProduct().getPrice());
            } else if (orderDetail.getService() != null) {
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

        // Розраховуємо total_price
        orderDetail.setPrice(
                orderDetail.getPrice().multiply(
                        BigDecimal.valueOf(orderDetail.getQuantity())
                )
        );

        return orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}