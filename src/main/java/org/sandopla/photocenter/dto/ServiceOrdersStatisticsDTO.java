package org.sandopla.photocenter.dto;

import lombok.Data;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiceOrdersStatisticsDTO {
    private Long branchId;
    private String branchName;
    private String serviceName;
    private Long regularOrdersCount = 0L;
    private Long urgentOrdersCount = 0L;
    private Long totalOrdersCount = 0L;

    public ServiceOrdersStatisticsDTO(Long branchId, String branchName, String serviceName,
                                      Long regularOrdersCount, Long urgentOrdersCount, Long totalOrdersCount) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.serviceName = serviceName;
        this.regularOrdersCount = regularOrdersCount;
        this.urgentOrdersCount = urgentOrdersCount;
        this.totalOrdersCount = totalOrdersCount;
    }
}