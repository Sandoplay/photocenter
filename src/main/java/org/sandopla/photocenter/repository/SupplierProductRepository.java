package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.Product;
import org.sandopla.photocenter.model.Supplier;
import org.sandopla.photocenter.model.SupplierProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {
    List<SupplierProduct> findBySupplier(Supplier supplier);
    List<SupplierProduct> findByProduct(Product product);
    Optional<SupplierProduct> findBySupplierAndProduct(Supplier supplier, Product product);
    List<SupplierProduct> findBySupplierAndActive(Supplier supplier, boolean active);
    Optional<SupplierProduct> findFirstByProductIdAndActiveTrue(Long productId);

}