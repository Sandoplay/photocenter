package org.sandopla.photocenter.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceRevenueDTO {
    private Long branchId;
    private String branchName;
    private String serviceName;
    private BigDecimal regularOrdersRevenue;
    private BigDecimal urgentOrdersRevenue;
    private BigDecimal totalRevenue;

    public ServiceRevenueDTO(Long branchId, String branchName, String serviceName,
                             Double regularOrdersRevenue, Double urgentOrdersRevenue, Double totalRevenue) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.serviceName = serviceName;
        this.regularOrdersRevenue = regularOrdersRevenue != null ? BigDecimal.valueOf(regularOrdersRevenue) : BigDecimal.ZERO;
        this.urgentOrdersRevenue = urgentOrdersRevenue != null ? BigDecimal.valueOf(urgentOrdersRevenue) : BigDecimal.ZERO;
        this.totalRevenue = totalRevenue != null ? BigDecimal.valueOf(totalRevenue) : BigDecimal.ZERO;
    }
}