package org.sandopla.photocenter.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesRevenueDto {
    private String branchName;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal profit;

    public SalesRevenueDto(String branchName, Double totalRevenue, Double totalCost) {
        this.branchName = branchName;
        this.totalRevenue = totalRevenue != null ? BigDecimal.valueOf(totalRevenue) : BigDecimal.ZERO;
        this.totalCost = totalCost != null ? BigDecimal.valueOf(totalCost) : BigDecimal.ZERO;
        this.profit = this.totalRevenue.subtract(this.totalCost);
    }
}