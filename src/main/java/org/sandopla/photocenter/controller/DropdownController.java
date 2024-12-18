package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Product;
import org.sandopla.photocenter.model.Service;
import org.sandopla.photocenter.service.BranchService;
import org.sandopla.photocenter.service.ProductService;
import org.sandopla.photocenter.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dropdown")
public class DropdownController {

    private final BranchService branchService;
    private final ProductService productService;
    private final PhotoService photoService;

    @Autowired
    public DropdownController(BranchService branchService,
                              ProductService productService,
                              PhotoService photoService) {
        this.branchService = branchService;
        this.productService = productService;
        this.photoService = photoService;
    }

    @GetMapping("/branches")
    public List<Branch> getBranches() {
        return branchService.getAllBranches(); // Змінено з getAllActiveBranches на getAllBranches
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/services")
    public List<Service> getServices() {
        return photoService.getAllServices();
    }
}