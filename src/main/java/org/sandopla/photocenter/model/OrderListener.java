package org.sandopla.photocenter.model;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.math.BigDecimal;

public class OrderListener {

    @PrePersist
    @PreUpdate
    public void calculateTotalCost(Order order) {
        BigDecimal totalCost = BigDecimal.ZERO;
        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                totalCost = totalCost.add(detail.getTotalPrice());
            }
        }
        order.setTotalCost(totalCost);
    }
}