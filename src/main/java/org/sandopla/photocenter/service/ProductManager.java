package org.sandopla.photocenter.service;

//import org.sandopla.photocenter.model.Manufacturer;
import org.sandopla.photocenter.model.Product;
//import org.sandopla.photocenter.repository.ManufacturerRepository;
import org.sandopla.photocenter.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductManager {
    private final ProductRepository productRepository;
    //private final ManufacturerRepository manufacturerRepository;

    @Autowired
    public ProductManager(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        // Перевірка обов'язкових полів
        if (product.getName() == null || product.getPrice() == null) {
            throw new IllegalArgumentException("Name and price are required");
        }

        // Перевірка і встановлення виробника якщо вказаний
//        if (product.getManufacturer() != null && product.getManufacturer().getId() != null) {
//            Manufacturer manufacturer = manufacturerRepository.findById(product.getManufacturer().getId())
//                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
//            product.setManufacturer(manufacturer);
//        }

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());

        // Оновлення виробника якщо вказаний
//        if (productDetails.getManufacturer() != null && productDetails.getManufacturer().getId() != null) {
//            Manufacturer manufacturer = manufacturerRepository.findById(productDetails.getManufacturer().getId())
//                    .orElseThrow(() -> new RuntimeException("Manufacturer not found"));
//            product.setManufacturer(manufacturer);
//        } else {
//            product.setManufacturer(null);
//        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        // Тут можна додати перевірку чи не використовується продукт в замовленнях
        productRepository.deleteById(id);
    }
}
