package org.sandopla.photocenter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";  // повертає шаблон home.html
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }
}