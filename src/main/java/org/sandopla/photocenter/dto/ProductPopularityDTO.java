package org.sandopla.photocenter.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductPopularityDTO {
    private Long productId;
    private String productName;
    private String supplierName;
    private Long salesCount;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private Double averageQuantityPerOrder;

    public ProductPopularityDTO(Long productId, String productName, Long salesCount,
                                Double revenue, Double supplierCost, Double avgQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.salesCount = salesCount;
        this.totalRevenue = revenue != null ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO;
        BigDecimal cost = supplierCost != null ? BigDecimal.valueOf(supplierCost) : BigDecimal.ZERO;
        this.totalProfit = this.totalRevenue.subtract(cost);
        this.averageQuantityPerOrder = avgQuantity;
    }
}