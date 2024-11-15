//package org.sandopla.photocenter.service;
//
//import org.sandopla.photocenter.model.Order;
//import org.sandopla.photocenter.model.OrderDetail;
//import jakarta.persistence.PrePersist;
//import jakarta.persistence.PreUpdate;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//
//public class OrderListener {
//
//    @PrePersist
//    @PreUpdate
//    public void calculateTotalCost(Order order) {
//        // Ініціалізуємо список, якщо він null
//        if (order.getOrderDetails() == null) {
//            order.setOrderDetails(new ArrayList<>());
//        }
//
//        for (OrderDetail detail : order.getOrderDetails()) {
//            // Перевіряємо і розраховуємо totalPrice для кожного OrderDetail
//            if (detail.getProduct() != null || detail.getService() != null) {
//                detail.calculateTotalPrice();
//            }
//        }
//
//        // Сумуємо totalPrice всіх OrderDetails
//        BigDecimal total = order.getOrderDetails().stream()
//                .map(detail -> detail.getTotalPrice() != null ? detail.getTotalPrice() : BigDecimal.ZERO)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        order.setTotalCost(total);
//    }
//}