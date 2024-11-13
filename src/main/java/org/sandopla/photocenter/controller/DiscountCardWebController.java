package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.DiscountCard;
import org.sandopla.photocenter.service.ClientService;
import org.sandopla.photocenter.service.DiscountCardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/discount-cards")
public class DiscountCardWebController {

    private final DiscountCardService discountCardService;
    private final ClientService clientService;

    public DiscountCardWebController(DiscountCardService discountCardService,
                                     ClientService clientService) {
        this.discountCardService = discountCardService;
        this.clientService = clientService;
    }

    @GetMapping
    public String discountCardsPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Client client = (Client) authentication.getPrincipal();
            List<DiscountCard> clientCards = discountCardService.getClientCards(client);
            boolean hasActiveCard = clientCards.stream()
                    .anyMatch(card -> card.isActive() &&
                            card.getExpiryDate().isAfter(LocalDate.now()));

            model.addAttribute("clientCards", clientCards);
            model.addAttribute("hasActiveCard", hasActiveCard);
            model.addAttribute("clientId", client.getId());
        }
        return "discount-cards";
    }

    @PostMapping("/buy")
    public String buyCard(@RequestParam Long clientId, RedirectAttributes redirectAttributes) {
        try {
            Client client = clientService.getClientById(clientId);
            discountCardService.createDiscountCard(client);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Дисконтну карту успішно придбано!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Помилка при створенні карти: " + e.getMessage());
        }
        return "redirect:/discount-cards";
    }

    @PostMapping("/extend")
    public String extendCard(@RequestParam String cardNumber, RedirectAttributes redirectAttributes) {
        try {
            discountCardService.extendValidity(cardNumber, 12); // продовжуємо на 12 місяців
            redirectAttributes.addFlashAttribute("successMessage",
                    "Термін дії карти успішно продовжено!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Помилка при продовженні карти: " + e.getMessage());
        }
        return "redirect:/discount-cards";
    }
}