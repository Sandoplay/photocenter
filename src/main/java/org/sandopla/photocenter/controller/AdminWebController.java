package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Role;
import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.service.BranchService;
import org.sandopla.photocenter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class AdminWebController {
    private final BranchService branchService;
    private final OrderService orderService;

    @Autowired
    public AdminWebController(BranchService branchService, OrderService orderService) {
        this.branchService = branchService;
        this.orderService = orderService;
    }

    @GetMapping
    public String adminHome(Model model, Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();

        if (client.getRole() == Role.OWNER) {
            model.addAttribute("branchesCount", branchService.getBranchesCount());
            model.addAttribute("totalRevenue", orderService.getTotalRevenue());
            model.addAttribute("branches", branchService.getAllBranches());
        } else {
            Branch adminBranch = client.getBranch();
            model.addAttribute("todayOrders", orderService.getTodayOrdersCount(adminBranch));
            model.addAttribute("branchRevenue", orderService.getBranchRevenue(adminBranch));
            model.addAttribute("kiosks", branchService.getKiosksForBranch(adminBranch.getId()));
        }

        model.addAttribute("lastOrders", orderService.getLastOrders(10));

        return "admin/admin-home";
    }

    @GetMapping("/users")
    public String userManagement(Model model) {
        return "admin/users";
    }

    @GetMapping("/branches")
    public String branchManagement(Model model, Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();

        if (client.getRole() == Role.OWNER) {
            model.addAttribute("branches", branchService.getAllBranches());
            model.addAttribute("mainOffices", branchService.getBranchesByType(Branch.BranchType.MAIN_OFFICE));
            model.addAttribute("branches", branchService.getBranchesByType(Branch.BranchType.BRANCH));
            model.addAttribute("kiosks", branchService.getBranchesByType(Branch.BranchType.KIOSK));
        } else {
            Branch adminBranch = client.getBranch();
            model.addAttribute("branch", adminBranch);
            model.addAttribute("kiosks", branchService.getKiosksForBranch(adminBranch.getId()));
        }

        return "admin/branches";
    }

    @GetMapping("/orders")
    public String orderManagement(Model model, Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();

        if (client.getRole() == Role.OWNER) {
            model.addAttribute("orders", orderService.getAllOrders());
        } else {
            Branch adminBranch = client.getBranch();
            model.addAttribute("orders", orderService.getBranchOrdersByDate(
                    adminBranch,
                    java.time.LocalDateTime.now().minusDays(30),
                    java.time.LocalDateTime.now()
            ));
        }

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

    @GetMapping("/global/branches")
    @PreAuthorize("hasRole('OWNER')")
    public String globalBranchManagement(Model model) {
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("branchTypes", Branch.BranchType.values());
        return "admin/global-branches";
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String branchManagement(@PathVariable Long branchId, Model model,
                                   Authentication authentication) {
        Client admin = (Client) authentication.getPrincipal();
        if (!admin.getBranch().getId().equals(branchId) &&
                admin.getRole() != Role.OWNER) {
            return "error/403";
        }

        Branch branch = branchService.getBranchById(branchId);
        model.addAttribute("branch", branch);
        model.addAttribute("kiosks", branchService.getKiosksForBranch(branchId));
        model.addAttribute("todayOrders", orderService.getTodayOrdersCount(branch));
        model.addAttribute("branchRevenue", orderService.getBranchRevenue(branch));

        return "admin/branch-management";
    }

    @PostMapping("/branches")
    @PreAuthorize("hasRole('OWNER')")
    public String createBranch(@ModelAttribute Branch branch) {
        branchService.createBranch(branch);
        return "redirect:/admin/branches";
    }

    @PostMapping("/branches/{id}/delete")
    @PreAuthorize("hasRole('OWNER')")
    public String deleteBranch(@PathVariable Long id) {
        if (branchService.canDeleteBranch(id)) {
            branchService.deleteBranch(id);
            return "redirect:/admin/branches";
        }
        // TODO: Додати обробку помилки, якщо філію не можна видалити
        return "redirect:/admin/branches?error=cannot-delete";
    }

    @PostMapping("/branches/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public String updateBranch(@PathVariable Long id, @ModelAttribute Branch branchDetails) {
        branchService.updateBranch(id, branchDetails);
        return "redirect:/admin/branches";
    }

    @GetMapping("/statistics")
    public String statistics(Model model, Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();

        if (client.getRole() == Role.OWNER) {
            model.addAttribute("totalRevenue", orderService.getTotalRevenue());
            model.addAttribute("allBranches", branchService.getAllBranches());
        } else {
            Branch adminBranch = client.getBranch();
            model.addAttribute("branchRevenue", orderService.getBranchRevenue(adminBranch));
            model.addAttribute("branch", adminBranch);
        }

        return "admin/statistics";
    }
}