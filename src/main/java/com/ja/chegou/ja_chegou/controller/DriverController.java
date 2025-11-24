package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Driver;
import com.ja.chegou.ja_chegou.enumerated.Role;
import com.ja.chegou.ja_chegou.enumerated.Status;
import com.ja.chegou.ja_chegou.repository.DriverRepository;
import com.ja.chegou.ja_chegou.repository.UsuariosRepository;
import com.ja.chegou.ja_chegou.utils.CnhUtils;
import com.ja.chegou.ja_chegou.utils.CpfUtils;
import com.ja.chegou.ja_chegou.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    DriverService driverService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private UsuariosRepository usuariosRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/usuarios_driver")
    public String listDrivers (Model model) {
        model.addAttribute("drivers", driverService.findAll());
        return "usuarios_driver";
    }

    @GetMapping("/new_driver")
    public String newDriver(Model model) {
        model.addAttribute("driver", new Driver());
        return "driver_form";
    }

    @PostMapping("/save")
    public String saveDriver(@ModelAttribute("driver") Driver driver,
                             @RequestParam("confPassword") String confPassword,
                             Model model) {
        if(!driver.getPassword().equals(confPassword)) {
            model.addAttribute("driver", driver);
            model.addAttribute("errorMessage", "As senhas não conferem");
            return "driver_form";
        }

        if(!CpfUtils.isValidCPF(driver.getCpf())) {
            model.addAttribute("driver", driver);
            model.addAttribute("errorMessage", "CPF invalido");
            return "driver_form";
        }
        if (usuariosRepository.findByCpf(driver.getCpf()).isPresent()) {
            model.addAttribute("driver", driver);
            model.addAttribute("errorMessage", "Já existe um usuário com este CPF!");
            return "driver_form";
        }
        if(usuariosRepository.findByEmail(driver.getEmail()).isPresent()) {
            model.addAttribute("driver", driver);
            model.addAttribute("errorMessage","Já existe um usuário com este email");
            return "driver_form";
        }
        if(!CnhUtils.isValidCNH(driver.getCnh())) {
            model.addAttribute("driver", driver);
            model.addAttribute("errorMessage", "CNH invalida");
            return "driver_form";
        }
        if(driver.getStatus() == null) {
            driver.setStatus(Status.ATIVO);
        }
        if (!CnhUtils.isValidCnhValidity(driver.getCnhValidity())){
            model.addAttribute("driver", driver);
            model.addAttribute("errorMessage", "A CNH está vencida!");
            return "driver_form";
        }

        driver.setCpf(driver.getCpf().replaceAll("\\D", ""));
        driver.setRole(Role.DRIVER);
        driver.setPassword(passwordEncoder.encode(driver.getPassword()));

        driverService.save(driver);
        return "redirect:/driver/usuarios_driver";
    }

    @PostMapping("/{id}/update")
    public String updateDriver(@PathVariable Long id,
                               @ModelAttribute("driver") Driver driverForm,
                               @RequestParam(required = false) String currentPassword,
                               @RequestParam(required = false) String newPassword,
                               @RequestParam(required = false) String confPassword,
                               Model model) {
        Driver driver = driverService.findById(id);

        driver.setName(driverForm.getName());
        driver.setEmail(driverForm.getEmail());
        driver.setCnhCategory(driverForm.getCnhCategory());
        driver.setCnhCategory(driverForm.getCnhCategory());
        driver.setCnhValidity(driverForm.getCnhValidity());

        if (newPassword != null && !newPassword.isBlank()) {
            if (currentPassword != null || !passwordEncoder.matches(currentPassword, driver.getPassword())) {
                model.addAttribute("driver", driverForm);
                model.addAttribute("errorMessage", "senha atual incorreta");
                return "edit_driver";
            }
            if (!newPassword.equals(confPassword)) {
                model.addAttribute("driver", driverForm);
                model.addAttribute("errorMessage", "A nova senha e a confirmação não coincidem");
                return "edit_driver";
            }
            driver.setPassword(passwordEncoder.encode(newPassword));
        }
        driverService.save(driver);
        return "redirect:/driver/usuarios_driver";
    }

    @GetMapping("/{id}/edit")
    public String editDriverForm(@PathVariable Long id, Model model) {
        Driver driver = driverService.findById(id);
        model.addAttribute("driver", driver);
        return "edit_driver";
    }


    @GetMapping("/{id}/status")
    public String changeDriverStatus(@PathVariable Long id) {
        driverService.alterarStatus(id);
        return "redirect:/driver/usuarios_driver";
    }

    @GetMapping("/{id}/delete")
    public String deleteDriver(@PathVariable Long id) {
        driverService.delete(id);
        return "redirect:/admin/driver";
    }
 }
