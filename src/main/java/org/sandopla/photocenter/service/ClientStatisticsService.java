package org.sandopla.photocenter.service;

import lombok.RequiredArgsConstructor;
import org.sandopla.photocenter.dto.ClientStatisticsDTO;
import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.repository.BranchRepository;
import org.sandopla.photocenter.repository.ClientRepository;
import org.sandopla.photocenter.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientStatisticsService {
    private final ClientRepository clientRepository;
    private final BranchRepository branchRepository;
    private final OrderRepository orderRepository;


    public List<ClientStatisticsDTO> getClientStatistics(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long branchId,
            BigDecimal minOrderAmount,
            Boolean hasDiscount) {
        return orderRepository.getClientStatistics(startDate, endDate, branchId, minOrderAmount, hasDiscount);
    }
}