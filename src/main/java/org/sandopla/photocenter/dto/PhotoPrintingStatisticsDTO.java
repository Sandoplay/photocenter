package org.sandopla.photocenter.dto;

import lombok.Data;

@Data
public class PhotoPrintingStatisticsDTO {
    private Long branchId;
    private String branchName;
    private Long regularPhotosCount = 0L;
    private Long urgentPhotosCount = 0L;
    private Long photosWithFramesCount = 0L;

    // Конструктор для JPA
    public PhotoPrintingStatisticsDTO(Long branchId, String branchName,
                                      Long regularPhotosCount, Long urgentPhotosCount, Long photosWithFramesCount) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.regularPhotosCount = regularPhotosCount != null ? regularPhotosCount : 0L;
        this.urgentPhotosCount = urgentPhotosCount != null ? urgentPhotosCount : 0L;
        this.photosWithFramesCount = photosWithFramesCount != null ? photosWithFramesCount : 0L;
    }

    public Long getTotalPhotosCount() {
        return regularPhotosCount + urgentPhotosCount + photosWithFramesCount;
    }
}