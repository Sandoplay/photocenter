package org.sandopla.photocenter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
public class AdminWebController {

    @GetMapping
    public String adminHome(Model model) {
        return "admin/admin-home";
    }

    @GetMapping("/users")
    public String userManagement(Model model) {
        return "admin/users";
    }

    @GetMapping("/branches")
    public String branchManagement(Model model) {
        return "admin/branches";
    }

    @GetMapping("/orders")
    public String orderManagement(Model model) {
        return "admin/orders";
    }

    @GetMapping("/products")
    public String productManagement(Model model) {
        return "admin/products";
    }

    @GetMapping("/services")
    public String serviceManagement(Model model) {
        return "admin/services";
    }
}