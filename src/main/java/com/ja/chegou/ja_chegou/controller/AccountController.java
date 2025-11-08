package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.AccountForm;
import com.ja.chegou.ja_chegou.entity.AccountUpdateForm;
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
    public String updateAccount(@ModelAttribute("userForm") AccountUpdateForm form,
                                BindingResult binding,
                                Model model,
                                Principal principal,
                                RedirectAttributes redirectAttrs) {

        String email = (principal != null) ? principal.getName() : form.getEmail();

        if (email == null || email.isBlank()) {
            model.addAttribute("err", "Sessão expirada. Faça login novamente.");
            return "redirect:/client/login";
        }

        if (binding.hasErrors()) {
            System.out.println("Erros de binding detectados no formulário de conta:");
            binding.getAllErrors().forEach(error -> {
                System.out.println(" - " + error);
            });
            model.addAttribute("err", "Existem erros no formulário.");
            return "account";
        }

        Client client = clientService.findByEmail(email);
        if (client == null) {
            model.addAttribute("err", "Cliente não encontrado.");
            return "account";
        }

        if (form.getName() != null && !form.getName().isBlank())
            client.setName(form.getName());

        if (form.getBirthDate() != null)
            client.setBirthDate(form.getBirthDate());

        if (form.getPhone() != null && !form.getPhone().isBlank())
            client.setPhone(form.getPhone());

        if (form.getCep() != null && !form.getCep().isBlank())
            client.setCep(form.getCep());

        if (form.getLogradouro() != null && !form.getLogradouro().isBlank())
            client.setLogradouro(form.getLogradouro());

        if (form.getHood() != null && !form.getHood().isBlank())
            client.setHood(form.getHood());

        if (form.getCity() != null && !form.getCity().isBlank())
            client.setCity(form.getCity());

        if (form.getState() != null && !form.getState().isBlank())
            client.setState(form.getState());

        if (form.getNumber() != null && !form.getNumber().isBlank())
            client.setNumber(form.getNumber());

        if (form.getComplement() != null && !form.getComplement().isBlank())
            client.setComplement(form.getComplement());

        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {

            if (!form.getNewPassword().equals(form.getConfirmPassword())) {
                model.addAttribute("err", "A nova senha e a confirmação não coincidem.");
                return "account";
            }

            if (form.getCurrentPassword() == null ||
                    !passwordEncoder.matches(form.getCurrentPassword(), client.getPassword())) {
                model.addAttribute("err", "Senha atual incorreta.");
                return "account";
            }

            client.setPassword(passwordEncoder.encode(form.getNewPassword()));
        }

        try {
            clientService.save(client);
            redirectAttrs.addFlashAttribute("msg", "Dados atualizados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("err", "Erro ao salvar os dados. Verifique os campos e tente novamente.");
            return "account";
        }

        return "redirect:/profile?email=" + email;
    }

}
