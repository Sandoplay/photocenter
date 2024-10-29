package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.*;
import org.sandopla.photocenter.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sandopla.photocenter.model.Role;

import java.math.BigDecimal;

@Service
public class DataInitializationService {

    private final ClientService clientService;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;

    @Autowired
    public DataInitializationService(ClientService clientService,
                                     BranchRepository branchRepository,
                                     ProductRepository productRepository,
                                     ServiceRepository serviceRepository) {
        this.clientService = clientService;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        if (clientService.getAllClients().isEmpty()) {
            initClients();
        }
        if (branchRepository.count() == 0) {
            initBranches();
        }
        if (productRepository.count() == 0) {
            initProducts();
        }
        if (serviceRepository.count() == 0) {
            initServices();
        }
    }

    private void initClients() {
        Client client1 = new Client();
        client1.setName("John Doe");
        client1.setPhoneNumber("1234567890");
        client1.setEmail("john@example.com");
        client1.setType(Client.ClientType.AMATEUR);
        client1.setUsername("john.doe");
        client1.setPassword("password123");
        client1.setRole(Role.USER);  // Змінено з "USER" на Role.USER
        clientService.createClient(client1);

        Client client2 = new Client();
        client2.setName("Jane Smith");
        client2.setPhoneNumber("0987654321");
        client2.setEmail("jane@example.com");
        client2.setType(Client.ClientType.PROFESSIONAL);
        client2.setUsername("jane.smith");
        client2.setPassword("password456");
        client2.setRole(Role.USER);  // Змінено з "USER" на Role.USER
        clientService.createClient(client2);
    }

    private void initBranches() {
        Branch branch1 = new Branch();
        branch1.setName("Main Office");
        branch1.setAddress("123 Main St");
        branch1.setWorkplaceCount(10);
        branch1.setType(Branch.BranchType.MAIN_OFFICE);
        branchRepository.save(branch1);

        Branch branch2 = new Branch();
        branch2.setName("Downtown Branch");
        branch2.setAddress("456 Downtown Ave");
        branch2.setWorkplaceCount(5);
        branch2.setType(Branch.BranchType.BRANCH);
        branchRepository.save(branch2);
    }

    private void initProducts() {
        Product product1 = new Product();
        product1.setName("Photo Paper 10x15");
        product1.setDescription("High quality photo paper");
        product1.setCategory("Printing Supplies");
        product1.setPrice(new BigDecimal("5.99"));
        product1.setStockQuantity(1000);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Photo Frame 20x30");
        product2.setDescription("Wooden photo frame");
        product2.setCategory("Frames");
        product2.setPrice(new BigDecimal("15.99"));
        product2.setStockQuantity(100);
        productRepository.save(product2);
    }

    private void initServices() {
        org.sandopla.photocenter.model.Service service1 = new org.sandopla.photocenter.model.Service();
        service1.setName("Photo Printing 10x15");
        service1.setDescription("Standard photo printing service");
        service1.setBasePrice(new BigDecimal("0.99"));
        service1.setUrgentPriceMultiplier(new BigDecimal("1.5"));
        serviceRepository.save(service1);

        org.sandopla.photocenter.model.Service service2 = new org.sandopla.photocenter.model.Service();
        service2.setName("Photo Restoration");
        service2.setDescription("Restore old or damaged photos");
        service2.setBasePrice(new BigDecimal("29.99"));
        service2.setUrgentPriceMultiplier(new BigDecimal("2.0"));
        serviceRepository.save(service2);
    }
}