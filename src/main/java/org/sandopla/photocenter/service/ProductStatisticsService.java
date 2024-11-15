package org.sandopla.photocenter.service;

import lombok.RequiredArgsConstructor;
import org.sandopla.photocenter.dto.ProductPopularityDTO;
import org.sandopla.photocenter.repository.ProductRepository;
import org.sandopla.photocenter.repository.SupplierProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStatisticsService {
    private final ProductRepository productRepository;
    private final SupplierProductRepository supplierProductRepository;


    @Transactional(readOnly = true)
    public List<ProductPopularityDTO> getMostPopularProducts(Long branchId, LocalDateTime startDate, LocalDateTime endDate) {
        List<ProductPopularityDTO> popularProducts = productRepository.findMostPopularProducts(branchId, startDate, endDate);

        popularProducts.forEach(product -> {
            supplierProductRepository.findFirstByProductIdAndActiveTrue(product.getProductId())
                    .ifPresent(sp -> product.setSupplierName(sp.getSupplier().getName()));
        });

        return popularProducts;
    }


}