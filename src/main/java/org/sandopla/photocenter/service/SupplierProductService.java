package org.sandopla.photocenter.service;

import org.sandopla.photocenter.dto.SupplierStatisticsDTO;
import org.sandopla.photocenter.model.Product;
import org.sandopla.photocenter.model.ProductCategory;
import org.sandopla.photocenter.model.Supplier;
import org.sandopla.photocenter.model.SupplierProduct;
import org.sandopla.photocenter.repository.ProductRepository;
import org.sandopla.photocenter.repository.SupplierProductRepository;
import org.sandopla.photocenter.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierProductService {
    private final SupplierProductRepository supplierProductRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SupplierProductService(SupplierProductRepository supplierProductRepository,
                                  SupplierRepository supplierRepository,
                                  ProductRepository productRepository) {
        this.supplierProductRepository = supplierProductRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    public List<SupplierProduct> getSupplierProducts(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return supplierProductRepository.findBySupplier(supplier);
    }

    public SupplierProduct addProductToSupplier(Long supplierId, Long productId, BigDecimal supplierPrice, Integer quantity) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Перевіряємо чи не існує вже такий продукт у постачальника
        if (supplierProductRepository.findBySupplierAndProduct(supplier, product).isPresent()) {
            throw new RuntimeException("This product is already added for this supplier");
        }

        SupplierProduct supplierProduct = new SupplierProduct();
        supplierProduct.setSupplier(supplier);
        supplierProduct.setProduct(product);
        supplierProduct.setSupplierPrice(supplierPrice);
        supplierProduct.setQuantity(quantity);

        return supplierProductRepository.save(supplierProduct);
    }



    public void updateSupplierPrice(Long supplierProductId, BigDecimal newPrice) {
        SupplierProduct supplierProduct = supplierProductRepository.findById(supplierProductId)
                .orElseThrow(() -> new RuntimeException("SupplierProduct not found"));
        supplierProduct.setSupplierPrice(newPrice);
        supplierProductRepository.save(supplierProduct);
    }

    public void removeProductFromSupplier(Long supplierProductId) {
        supplierProductRepository.deleteById(supplierProductId);
    }

    public List<Product> getAvailableProductsForSupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        List<SupplierProduct> existingProducts = supplierProductRepository.findBySupplier(supplier);
        List<Long> existingProductIds = existingProducts.stream()
                .map(sp -> sp.getProduct().getId())
                .collect(Collectors.toList());

        return productRepository.findAll().stream()
                .filter(p -> !existingProductIds.contains(p.getId()))
                .collect(Collectors.toList());
    }
}