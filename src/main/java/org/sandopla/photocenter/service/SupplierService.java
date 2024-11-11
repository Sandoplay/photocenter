package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.ProductCategory;
import org.sandopla.photocenter.model.Supplier;
import org.sandopla.photocenter.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    public List<Supplier> getSuppliersByCategory(ProductCategory category) {
        return supplierRepository.findBySpecializationsContaining(category);
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        // Встановлюємо значення за замовчуванням якщо потрібно
        if (supplier.getRating() == null) {
            supplier.setRating(0);
        }
        if (supplier.getSpecializations() == null) {
            supplier.setSpecializations(new HashSet<>());
        }
        supplier.setActive(true);
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier supplierDetails) {
        Supplier supplier = getSupplierById(id);

        supplier.setName(supplierDetails.getName());
        supplier.setContactPerson(supplierDetails.getContactPerson());
        supplier.setPhoneNumber(supplierDetails.getPhoneNumber());
        supplier.setEmail(supplierDetails.getEmail());
        supplier.setAddress(supplierDetails.getAddress());
        supplier.setSpecializations(supplierDetails.getSpecializations());
        supplier.setRating(supplierDetails.getRating());
        supplier.setNotes(supplierDetails.getNotes());
        supplier.setActive(supplierDetails.isActive());

        return supplierRepository.save(supplier);
    }

    @Transactional
    public void toggleSupplierActive(Long id) {
        Supplier supplier = getSupplierById(id);
        supplier.setActive(!supplier.isActive());
        supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }

    public boolean hasDeliveries(Long id) {
        // TODO: Реалізувати перевірку наявності поставок
        return false;
    }
}