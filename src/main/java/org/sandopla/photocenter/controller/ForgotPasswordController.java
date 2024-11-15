package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.service.ClientService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    private final ClientService clientService;

    public ForgotPasswordController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping
    public String processForgotPassword(@RequestParam("username") String username,
                                        @RequestParam("newPassword") String newPassword,
                                        RedirectAttributes redirectAttributes) {
        try {
            clientService.updatePassword(username, newPassword);
            redirectAttributes.addFlashAttribute("message", "Your password has been updated successfully");
        } catch (UsernameNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid username");
        }
        return "redirect:/login";
    }
}