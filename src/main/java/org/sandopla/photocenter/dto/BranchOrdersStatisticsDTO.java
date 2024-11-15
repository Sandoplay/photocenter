package org.sandopla.photocenter.dto;

import lombok.Data;
import org.sandopla.photocenter.model.Branch;

@Data
public class BranchOrdersStatisticsDTO {
    private Long branchId;
    private String branchName;
    private String branchType;  // MAIN_OFFICE, BRANCH або KIOSK
    private Long parentBranchId;
    private String parentBranchName;
    private Long ordersCount = 0L;

    public BranchOrdersStatisticsDTO(Long branchId, String branchName, Branch.BranchType branchType,
                                     Long parentBranchId, String parentBranchName, Long ordersCount) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.branchType = branchType.name(); // Convert enum to String
        this.parentBranchId = parentBranchId;
        this.parentBranchName = parentBranchName;
        this.ordersCount = ordersCount;
    }
}