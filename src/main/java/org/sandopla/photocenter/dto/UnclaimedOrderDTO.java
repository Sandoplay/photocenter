package org.sandopla.photocenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sandopla.photocenter.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UnclaimedOrderDTO {
    private Long orderId;
    private String branchName;
    private String branchType;  // змінили з Branch.BranchType на String
    private String parentBranchName;
    private String clientName;
    private String clientPhone;
    private LocalDateTime orderDate;
    private LocalDateTime completionDate;
    private BigDecimal totalCost;
    private Order.OrderStatus status;
}