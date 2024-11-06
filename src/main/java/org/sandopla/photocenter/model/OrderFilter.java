package org.sandopla.photocenter.model;

import lombok.Data;

// Клас для фільтрації замовлень
@Data
public class OrderFilter {
    private Order.OrderStatus status;
    private Boolean urgent;
    private String search;
}