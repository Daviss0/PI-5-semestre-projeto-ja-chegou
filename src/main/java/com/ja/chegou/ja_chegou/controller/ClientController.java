package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Client;
import com.ja.chegou.ja_chegou.repository.ClientRepository;
import com.ja.chegou.ja_chegou.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping({"/", "/mainPage"})
    public String homePage(Model model, Principal principal) {
        if (principal != null) {
            Optional<Client> optClient = clientRepository.findByEmail(principal.getName());

            if (optClient.isPresent()) {
                Client client = optClient.get();
                model.addAttribute("logged", true);
                model.addAttribute("clientLat", client.getLatitude());
                model.addAttribute("clientLng", client.getLongitude());
                model.addAttribute("clientName", client.getName());
            } else {
                model.addAttribute("logged", false);
            }
        } else {
            model.addAttribute("logged", false);
        }
        return "mainPage";
    }

    @GetMapping("/client/login")
    public String login() {
        return "login_client";
    }


    @GetMapping("/register")
    public String register() {
        return "registerClient";
    }

    @GetMapping("/profile")
    public String profile(@RequestParam(required = false) String email, Model model) {
        Client client = null;
        if (email != null) {
            client = clientService.findByEmail(email);
        }
        if (client == null) {
            return "redirect:/client/login";
        }
        model.addAttribute("userForm", client);
        return "account";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("userForm") Client client) {
        try {
            clientService.update(client);
            return "redirect:/mainPage?updated=true";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/profile?error=true";
        }
    }

}
