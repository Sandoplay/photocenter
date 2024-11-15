package org.sandopla.photocenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductOrdersStatisticsDTO {
    private Long branchId;
    private String branchName;
    private String productName;
    private Long regularOrdersCount;
    private Long urgentOrdersCount;
    private Long totalOrdersCount;
}