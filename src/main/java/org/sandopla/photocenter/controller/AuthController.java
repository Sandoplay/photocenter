package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final ClientService clientService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(ClientService clientService, AuthenticationManager authenticationManager) {
        this.clientService = clientService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("client", new Client());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUpClient(@ModelAttribute("client") Client client, Model model) {
        try {
            Client registeredClient = clientService.registerNewClient(client);
            return "redirect:/auth/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> authenticateClient(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Client client = (Client) authentication.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("id", client.getId());
            response.put("username", client.getUsername());
            response.put("email", client.getEmail());
            response.put("role", client.getRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    @GetMapping("/api/me")
    @ResponseBody
    public ResponseEntity<?> getCurrentClient(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body("Not authenticated");
        }

        Client client = (Client) authentication.getPrincipal();
        return ResponseEntity.ok(client);
    }

    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<?> logoutClient() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }
}

class LoginRequest {
    private String username;
    private String password;

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}