package org.sandopla.photocenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sandopla.photocenter.model.ProductCategory;
import java.util.Set;

@Data
@AllArgsConstructor
public class SupplierStatisticsDTO {
    private Long supplierId;
    private String supplierName;
    private String category;
    private Long totalQuantity;
    private Long totalDeliveries;
}