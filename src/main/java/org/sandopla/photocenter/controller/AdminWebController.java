package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.*;
import org.sandopla.photocenter.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class AdminWebController {
    private final BranchService branchService;
    private final OrderService orderService;
    private final ProductService productService;
    private final PhotoServiceManager photoServiceManager;
    private final ClientService clientService;

    @Autowired
    public AdminWebController(BranchService branchService, OrderService orderService, ProductService productService,
                              PhotoServiceManager photoServiceManager, ClientService clientService) {
        this.branchService = branchService;
        this.orderService = orderService;
        this.productService = productService;
        this.photoServiceManager = photoServiceManager;
        this.clientService = clientService;
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

        // Для всіх користувачів отримуємо останні 10 замовлень
        if (client.getRole() == Role.OWNER) {
            model.addAttribute("lastOrders", orderService.getLastOrders(10));
        } else {
            Branch adminBranch = client.getBranch();
            model.addAttribute("lastOrders", orderService.getLastOrdersForBranch(adminBranch, 10));
        }

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

    @GetMapping("/admins")
    @PreAuthorize("hasRole('OWNER')")
    public String adminManagement(Model model) {
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("admins", clientService.getAllAdmins());  // Додаємо цей рядок
        return "admin/admins";
    }

    @GetMapping("/products")
    public String productManagement(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/services")
    public String serviceManagement(Model model) {
        model.addAttribute("services", photoServiceManager.getAllServices());
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

    @GetMapping("/orders")
    public String orderManagement(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean urgent,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable,
            Model model,
            Authentication authentication) {

        Client client = (Client) authentication.getPrincipal();

        // Створюємо об'єкт фільтру
        OrderFilter filter = new OrderFilter();
        if (status != null && !status.isEmpty()) {
            try {
                filter.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ігноруємо невірний статус
            }
        }
        filter.setUrgent(urgent);
        filter.setSearch(search);

        // Отримуємо замовлення з урахуванням ролі користувача та кіосків
        List<Order> orders;
        if (client.getRole() == Role.OWNER) {
            orders = orderService.findAllWithFilters(filter, pageable);
        } else {
            // Для адміністратора філії отримуємо замовлення включно з кіосками
            Branch adminBranch = client.getBranch();
            orders = orderService.findByBranchWithFilters(adminBranch, filter, pageable);
        }

        // Додаємо статистику
        long totalOrders = orders.size();
        long newOrders = orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.NEW).count();
        long inProgressOrders = orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.IN_PROGRESS).count();
        long completedOrders = orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED).count();

        // Додаємо всі необхідні атрибути до моделі
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("newOrders", newOrders);
        model.addAttribute("inProgressOrders", inProgressOrders);
        model.addAttribute("completedOrders", completedOrders);

        // Додаємо параметри фільтрації для відображення в UI
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentUrgent", urgent);
        model.addAttribute("currentSearch", search);

        // Додаємо всі можливі статуси для фільтра
        model.addAttribute("orderStatuses", Order.OrderStatus.values());

        return "admin/orders-page";
    }
}