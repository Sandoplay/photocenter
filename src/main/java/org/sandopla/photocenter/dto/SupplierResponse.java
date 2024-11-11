package org.sandopla.photocenter.dto;

import lombok.Data;
import org.sandopla.photocenter.model.ProductCategory;
import java.util.Set;

@Data
public class SupplierResponse {
    private Long id;
    private String name;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
    private Set<ProductCategory> specializations;
    private boolean active;
    private Integer rating;
    private String notes;
    private Long totalDeliveries; // Кількість поставок
    private Double averageDeliveryTime; // Середній час виконання поставки
}