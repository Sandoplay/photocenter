package org.sandopla.photocenter.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSalesDTO {
    private Long productId;
    private String productName;
    private String category;
    private Long quantitySold;
    private BigDecimal totalRevenue;
    private String branchName;

    public ProductSalesDTO(
            Long productId,
            String productName,
            String category,
            Long quantitySold,
            BigDecimal totalRevenue,
            String branchName
    ) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantitySold = quantitySold;
        this.totalRevenue = totalRevenue;
        this.branchName = branchName;
    }
}