package org.sandopla.photocenter.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @PrePersist
    @PreUpdate
    private void calculateTotalPrice() {
        this.totalPrice = this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}