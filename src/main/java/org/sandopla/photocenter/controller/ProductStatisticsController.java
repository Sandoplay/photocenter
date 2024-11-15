package org.sandopla.photocenter.controller;

import lombok.RequiredArgsConstructor;
import org.sandopla.photocenter.dto.ProductPopularityDTO;
import org.sandopla.photocenter.service.ProductStatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics/products")
@RequiredArgsConstructor
public class ProductStatisticsController {
    private final ProductStatisticsService productStatisticsService;

    @GetMapping("/popular")
    public ResponseEntity<List<ProductPopularityDTO>> getMostPopularProducts(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(productStatisticsService.getMostPopularProducts(branchId, startDate, endDate));
    }
}