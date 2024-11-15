package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.dto.ClientStatisticsDTO;
import org.sandopla.photocenter.service.ClientStatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/statistics/clients")
public class ClientStatisticsController {
    private final ClientStatisticsService clientStatisticsService;

    public ClientStatisticsController(ClientStatisticsService clientStatisticsService) {
        this.clientStatisticsService = clientStatisticsService;
    }


    //Одержати список клієнтів у цілому по фотоцентру, клієнтів зазначеної філії, що
    //мають знижки, що зробили замовлення певного обсягу.
    @GetMapping
    public ResponseEntity<List<ClientStatisticsDTO>> getClientStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false, defaultValue = "1000") BigDecimal minOrderAmount,
            @RequestParam(required = false) Boolean hasDiscount) {

        return ResponseEntity.ok(
                clientStatisticsService.getClientStatistics(startDate, endDate, branchId, minOrderAmount, hasDiscount)
        );
    }
}
