package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.Supplier;
import org.sandopla.photocenter.model.ProductCategory;
import org.sandopla.photocenter.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/suppliers")
@PreAuthorize("hasRole('OWNER')")
public class SupplierAdminController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierAdminController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public String listSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("categories", ProductCategory.values());
        return "admin/suppliers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("categories", ProductCategory.values());
        return "admin/suppliers/form";
    }

    @PostMapping
    public String createSupplier(@ModelAttribute Supplier supplier) {
        supplierService.createSupplier(supplier);
        return "redirect:/admin/suppliers";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.getSupplierById(id));
        model.addAttribute("categories", ProductCategory.values());
        return "admin/suppliers/form";
    }

    @PostMapping("/{id}")
    public String updateSupplier(@PathVariable Long id, @ModelAttribute Supplier supplier) {
        supplierService.updateSupplier(id, supplier);
        return "redirect:/admin/suppliers";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id) {
        supplierService.toggleSupplierActive(id);
        return "redirect:/admin/suppliers";
    }
}