package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.service.SupplierProductService;
import org.sandopla.photocenter.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/suppliers/{supplierId}/products")
@PreAuthorize("hasRole('OWNER')")
public class SupplierProductController {

    private final SupplierProductService supplierProductService;
    private final SupplierService supplierService;

    @Autowired
    public SupplierProductController(SupplierProductService supplierProductService,
                                     SupplierService supplierService) {
        this.supplierProductService = supplierProductService;
        this.supplierService = supplierService;
    }

    @GetMapping
    public String listSupplierProducts(@PathVariable Long supplierId, Model model) {
        model.addAttribute("supplier", supplierService.getSupplierById(supplierId));
        model.addAttribute("supplierProducts", supplierProductService.getSupplierProducts(supplierId));
        model.addAttribute("availableProducts", supplierProductService.getAvailableProductsForSupplier(supplierId));
        return "admin/suppliers/products";
    }

    @PostMapping("/add")
    public String addProduct(@PathVariable Long supplierId,
                             @RequestParam Long productId,
                             @RequestParam BigDecimal supplierPrice) {
        supplierProductService.addProductToSupplier(supplierId, productId, supplierPrice);
        return "redirect:/admin/suppliers/" + supplierId + "/products";
    }

    @PostMapping("/{productId}/remove")
    public String removeProduct(@PathVariable Long supplierId,
                                @PathVariable Long productId) {
        supplierProductService.removeProductFromSupplier(productId);
        return "redirect:/admin/suppliers/" + supplierId + "/products";
    }

    @PostMapping("/{productId}/update-price")
    public String updatePrice(@PathVariable Long supplierId,
                              @PathVariable Long productId,
                              @RequestParam BigDecimal newPrice) {
        supplierProductService.updateSupplierPrice(productId, newPrice);
        return "redirect:/admin/suppliers/" + supplierId + "/products";
    }
}
