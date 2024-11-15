package org.sandopla.photocenter.service;

import lombok.extern.slf4j.Slf4j;
import org.sandopla.photocenter.dto.ProductSalesDTO;
import org.sandopla.photocenter.dto.SalesRevenueDto;
import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.repository.BranchRepository;
import org.sandopla.photocenter.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ProductSalesService {
    private final OrderRepository orderRepository;
    private final BranchRepository branchRepository;

    public ProductSalesService(OrderRepository orderRepository, BranchRepository branchRepository) {
        this.orderRepository = orderRepository;
        this.branchRepository = branchRepository;

    }

    public List<ProductSalesDTO> getProductSales(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long branchId) {
        return orderRepository.getProductSales(startDate, endDate, branchId);
    }

    public List<SalesRevenueDto> getSalesRevenue(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long branchId
    ) {
        log.info("Getting sales revenue for period: {} to {}, branchId: {}",
                startDate, endDate, branchId);

        List<SalesRevenueDto> results = orderRepository.getSalesRevenue(startDate, endDate, branchId);

        log.info("Found {} results", results.size());
        if (results.isEmpty()) {
            log.warn("No sales revenue data found for the specified parameters");
        } else {
            results.forEach(dto ->
                    log.info("Branch: {}, Revenue: {}", dto.getBranchName(), dto.getTotalRevenue())
            );
        }

        return results;
    }



}