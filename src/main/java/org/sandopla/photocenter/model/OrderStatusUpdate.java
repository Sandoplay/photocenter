package org.sandopla.photocenter.model;

public class OrderStatusUpdate {
    private Order.OrderStatus status;

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }
}