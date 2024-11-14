package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final OrderService orderService;

    @Autowired
    public HomeController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Client client = (Client) authentication.getPrincipal();
            List<Order> recentOrders = orderService.getRecentOrdersByClient(client, 10); // Отримуємо 5 останніх замовлень
            model.addAttribute("recentOrders", recentOrders);
        }
        return "home";
    }

    @GetMapping("/create-order")
    public String createOrder() {
        return "create-order";  // це вказує на templates/create-order.html
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }
}