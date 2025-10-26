package com.ja.chegou.ja_chegou.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ClientController {

    @GetMapping({"/","/mainPage"})
    public String homePage(Model model, Principal principal) {
        model.addAttribute("logged", principal != null);
        return "mainPage";
    }
}
