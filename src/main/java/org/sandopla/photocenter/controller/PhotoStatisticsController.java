package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.dto.PhotoStatisticsDTO;
import org.sandopla.photocenter.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics/photos")
public class PhotoStatisticsController {
    private final StatisticsService statisticsService;

    public PhotoStatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<List<PhotoStatisticsDTO>> getPhotoStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId) {

        return ResponseEntity.ok(
                statisticsService.getPhotoStatistics(startDate, endDate, branchId)
        );
    }

    @GetMapping("/total")
    public ResponseEntity<PhotoStatisticsDTO> getTotalPhotoStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return ResponseEntity.ok(
                statisticsService.getTotalPhotoStatistics(startDate, endDate)
        );
    }
}