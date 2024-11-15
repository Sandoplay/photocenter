package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.dto.ProductPopularityDTO;
import org.sandopla.photocenter.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);

    @Query("""
    SELECT NEW org.sandopla.photocenter.dto.ProductPopularityDTO(
        p.id,
        p.name, 
        COUNT(od.id),
        CAST(SUM(od.price * od.quantity) AS double),
        CAST(SUM(sp.supplierPrice * od.quantity) AS double),
        CAST(AVG(od.quantity) AS double))
    FROM OrderDetail od
    JOIN od.product p
    JOIN od.order o
    JOIN p.supplierProducts sp
    WHERE (:branchId IS NULL OR o.branch.id = :branchId)
    AND o.status = 'COMPLETED'
    AND sp.active = true
    AND (CAST(:startDate AS timestamp) IS NULL OR o.orderDate >= :startDate)
    AND (CAST(:endDate AS timestamp) IS NULL OR o.orderDate <= :endDate)
    GROUP BY p.id, p.name
    ORDER BY COUNT(od.id) DESC
""")
    List<ProductPopularityDTO> findMostPopularProducts(
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
