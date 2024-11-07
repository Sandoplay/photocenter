package org.sandopla.photocenter.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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
    @ToString.Exclude
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;  // Ціна за одиницю


//    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
//    private BigDecimal totalPrice = BigDecimal.ZERO;

//    @PrePersist
//    @PreUpdate
//    private void calculateTotalPrice() {
//        if (price != null && quantity != null) {
//            this.price = price.multiply(BigDecimal.valueOf(quantity));
//        }
//    }
}