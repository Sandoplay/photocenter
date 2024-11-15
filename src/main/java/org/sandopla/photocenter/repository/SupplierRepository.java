package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.ProductCategory;
import org.sandopla.photocenter.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByActiveTrue();

    List<Supplier> findBySpecializationsContaining(ProductCategory category);

    // Можливі додаткові методи для пошуку
    List<Supplier> findByNameContainingIgnoreCase(String name);

    boolean existsByPhoneNumber(String phoneNumber);

    List<Supplier> findByRatingGreaterThanEqual(Integer rating);

    @Query("""
    SELECT s.id AS supplierId, s.name AS supplierName, sp.product.category AS category, 
           SUM(sp.quantity) AS totalQuantity, COUNT(sp) AS totalDeliveries  
    FROM Supplier s
    LEFT JOIN SupplierProduct sp ON s.id = sp.supplier.id
    WHERE (:category IS NULL OR sp.product.category = :category)
    GROUP BY s.id, s.name, sp.product.category
    HAVING (:minDeliveries IS NULL OR COUNT(sp) >= :minDeliveries)
""")
    List<Object[]> findSuppliersByCriteria(
            @Param("category") String category,
            @Param("minDeliveries") Long minDeliveries);

}