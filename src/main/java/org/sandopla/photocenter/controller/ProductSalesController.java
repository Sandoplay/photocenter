package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.dto.ProductSalesDTO;
import org.sandopla.photocenter.dto.SalesRevenueDto;
import org.sandopla.photocenter.service.ProductSalesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics/sales")
public class ProductSalesController {
    private final ProductSalesService productSalesService;

    public ProductSalesController(ProductSalesService productSalesService) {
        this.productSalesService = productSalesService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductSalesDTO>> getProductSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId) {

        return ResponseEntity.ok(
                productSalesService.getProductSales(startDate, endDate, branchId)
        );
    }

    @GetMapping("/revenue")
    public ResponseEntity<List<SalesRevenueDto>> getSalesRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId
    ) {
        return ResponseEntity.ok(
                productSalesService.getSalesRevenue(startDate, endDate, branchId)
        );
    }

}