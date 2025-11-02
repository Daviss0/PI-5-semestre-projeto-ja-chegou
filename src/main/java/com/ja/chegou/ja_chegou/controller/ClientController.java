package com.ja.chegou.ja_chegou.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ClientController {

    @GetMapping({"/","/mainPage"})
    public String homePage(Model model, Principal principal) {
        model.addAttribute("logged", false);
        return "mainPage";
    }

    @GetMapping("/login")
    public String login () {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }
}
