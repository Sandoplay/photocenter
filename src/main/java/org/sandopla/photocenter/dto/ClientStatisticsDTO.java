package org.sandopla.photocenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sandopla.photocenter.model.Branch;
import org.springframework.data.annotation.PersistenceConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClientStatisticsDTO {
    private String branchName;
    private String clientName;
    private String phoneNumber;  // додаємо
    private String email;       // додаємо
    private BigDecimal totalOrdersAmount;
    private Long ordersCount;   // додаємо
    private Boolean hasDiscount;

    public ClientStatisticsDTO(
            String branchName,
            String clientName,
            String phoneNumber,
            String email,
            BigDecimal totalOrdersAmount,
            Long ordersCount,
            Boolean hasDiscount) {
        this.branchName = branchName;
        this.clientName = clientName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.totalOrdersAmount = totalOrdersAmount;
        this.ordersCount = ordersCount;
        this.hasDiscount = hasDiscount;
    }
}