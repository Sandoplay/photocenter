package org.sandopla.photocenter.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "supplier_products")
@Data
public class SupplierProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "supplier_price", nullable = false)
    private BigDecimal supplierPrice;

    @Column(nullable = false)
    private boolean active = true;

    private String notes;  // Примітки щодо поставки цього продукту

    @Column(nullable = false)
    private Integer quantity;
}