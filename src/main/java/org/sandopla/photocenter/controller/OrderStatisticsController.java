package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.dto.BranchOrdersStatisticsDTO;
import org.sandopla.photocenter.dto.ServiceOrdersStatisticsDTO;
import org.sandopla.photocenter.dto.ServiceRevenueDTO;
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
@RequestMapping("/api/statistics/orders")
public class OrderStatisticsController {
    private final StatisticsService statisticsService;

    public OrderStatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceOrdersStatisticsDTO>> getServiceOrdersStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId) {

        return ResponseEntity.ok(
                statisticsService.getServiceOrdersStatistics(startDate, endDate, branchId)
        );
    }

    @GetMapping("/by-branches")
    public ResponseEntity<List<BranchOrdersStatisticsDTO>> getBranchOrdersStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return ResponseEntity.ok(
                statisticsService.getBranchOrdersStatistics(startDate, endDate)
        );
    }

    @GetMapping("/service-revenue")
    public ResponseEntity<List<ServiceRevenueDTO>> getServiceRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId) {

        return ResponseEntity.ok(
                statisticsService.getServiceRevenue(startDate, endDate, branchId)
        );
    }
}