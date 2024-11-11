package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.ProductCategory;
import org.sandopla.photocenter.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByActiveTrue();
    List<Supplier> findBySpecializationsContaining(ProductCategory category);

    // Можливі додаткові методи для пошуку
    List<Supplier> findByNameContainingIgnoreCase(String name);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Supplier> findByRatingGreaterThanEqual(Integer rating);
}