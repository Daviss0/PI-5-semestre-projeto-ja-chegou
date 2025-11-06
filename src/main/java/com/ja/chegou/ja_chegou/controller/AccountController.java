package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.AccountForm;
import com.ja.chegou.ja_chegou.entity.Client;
import com.ja.chegou.ja_chegou.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AccountController {

    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;

    public AccountController(ClientService clientService, PasswordEncoder passwordEncoder) {
        this.clientService = clientService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/account")
    public String showAccountPage(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("err", "Sessão expirada. Faça login novamente.");
            return "redirect:/login";
        }

        Client client = clientService.findByEmail(principal.getName());
        if (client == null) {
            model.addAttribute("err", "Cliente não encontrado.");
            return "redirect:/login";
        }

        model.addAttribute("userForm", AccountForm.fromClient(client));
        return "account";
    }

    @PostMapping("/account")
    public String updateAccount(@Valid @ModelAttribute("userForm") AccountForm form,
                                BindingResult binding,
                                Model model,
                                Principal principal,
                                RedirectAttributes redirectAttrs) {

        if (principal == null) {
            model.addAttribute("err", "Sessão expirada. Faça login novamente.");
            return "redirect:/login";
        }

        if (binding.hasErrors()) {
            model.addAttribute("err", "Existem erros no formulário.");
            return "account";
        }

        Client client = clientService.findByEmail(principal.getName());
        if (client == null) {
            model.addAttribute("err", "Cliente não encontrado.");
            return "account";
        }

        client.setName(form.getName());
        client.setBirthDate(form.getBirthDate());
        client.setPhone(form.getPhone());
        client.setCep(form.getCep());
        client.setLogradouro(form.getLogradouro());
        client.setHood(form.getHood());
        client.setCity(form.getCity());
        client.setState(form.getState());

        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {

            if (!form.getNewPassword().equals(form.getConfirmPassword())) {
                model.addAttribute("err", "A nova senha e a confirmação não coincidem.");
                return "account";
            }

            if (form.getCurrentPassword() == null ||
                    !passwordEncoder.matches(form.getCurrentPassword(), client.getPasswordHash())) {
                model.addAttribute("err", "Senha atual incorreta.");
                return "account";
            }

            client.setPasswordHash(passwordEncoder.encode(form.getNewPassword()));
        }

        clientService.save(client);

        redirectAttrs.addFlashAttribute("msg", "Dados atualizados com sucesso!");
        return "redirect:/account";
    }
}
