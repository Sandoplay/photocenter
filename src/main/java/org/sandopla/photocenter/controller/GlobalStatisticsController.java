package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.dto.ProductOrdersStatisticsDTO;
import org.sandopla.photocenter.dto.UnclaimedOrderDTO;
import org.sandopla.photocenter.service.BranchService;
import org.sandopla.photocenter.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/global-statistics")
@PreAuthorize("hasRole('OWNER')")
public class GlobalStatisticsController {

    private final StatisticsService statisticsService;
    private final BranchService branchService;

    @Autowired
    public GlobalStatisticsController(StatisticsService statisticsService,
                                      BranchService branchService) {
        this.statisticsService = statisticsService;
        this.branchService = branchService;
    }

    @GetMapping
    public String showGlobalStatistics(Model model) {
        return "admin/global-statistics";
    }

    @GetMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<?> getOrdersStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId) {
        // TODO: Implement orders statistics
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/revenue")
    @ResponseBody
    public ResponseEntity<?> getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId) {
        // TODO: Implement revenue statistics
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/unclaimed-orders")
    public ResponseEntity<List<UnclaimedOrderDTO>> getUnclaimedOrders(
            @RequestParam(required = false) Long branchId) {
        return ResponseEntity.ok(statisticsService.getUnclaimedOrders(branchId));
    }


    @GetMapping("/api/product-orders")
    public ResponseEntity<List<ProductOrdersStatisticsDTO>> getProductOrdersStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId) {

        List<ProductOrdersStatisticsDTO> statistics =
                statisticsService.getProductOrdersStatistics(startDate, endDate, branchId);

        return ResponseEntity.ok(statistics);
    }
}