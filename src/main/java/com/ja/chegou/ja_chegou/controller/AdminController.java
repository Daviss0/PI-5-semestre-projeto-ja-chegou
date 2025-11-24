package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Admin;
import com.ja.chegou.ja_chegou.enumerated.Status;
import com.ja.chegou.ja_chegou.repository.AdminRepository;
import com.ja.chegou.ja_chegou.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private AdminService adminService;
    private AdminRepository adminRepository;

  public AdminController(AdminService adminService, AdminRepository adminRepository) {
    this.adminService = adminService;
    this.adminRepository = adminRepository;
  }

  @GetMapping("/login_adm")
  public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
    if (error != null) {
      model.addAttribute("loginError", error);
    }
    return "login_adm";
  }

  @GetMapping("/home")
  public String homePage(Model model, Principal principal) {
    String email = principal.getName();
    Admin admin = adminRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    model.addAttribute("adminName", admin.getName());
    return "home";
  }

  @GetMapping("/menu_users")
  public String menuUsers() {
    return "menu_users";
  }

  @GetMapping("/usuarios/{id}/toggle-status")
  public String toggleStatus(@PathVariable Long id) {
    Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    admin.setStatus(admin.getStatus() == Status.ATIVO ? Status.INATIVO : Status.ATIVO);
    adminRepository.save(admin);

    return "redirect:/admin/usuarios"; // volta para a lista de admins
  }


  @GetMapping("/usuarios")
  public String listUsers(Model model) {
    model.addAttribute("admins", adminRepository.findAll());
    return "usuarios_adm";
  }

  @GetMapping("/usuarios/new")
  public String newAdminForm(Model model) {
    model.addAttribute("admin", new Admin());
    model.addAttribute("isEdit", false);
    return "admin_form";
  }

  @GetMapping("/usuarios/{id}/edit")
  public String editAdminForm(@PathVariable Long id, Model model) {
    Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    model.addAttribute("admin", admin);
    model.addAttribute("isEdit", true);
    return "admin_form";
  }

  @GetMapping("/editar/{id}")
  public String showEditForm(@PathVariable Long id, Model model) {
    Admin admin = adminService.searchById(id);

    System.out.println(">>> Admin carregado para edição: ID=" + admin.getId()
            + ", Nome=" + admin.getName()
            + ", Email=" + admin.getEmail());

    model.addAttribute("admin", admin);
    return "edit_adm";
  }

  @PostMapping("/editar/{id}")
  public String saveEdit(@PathVariable Long id,
                         @ModelAttribute("admin") Admin adminUpdated,
                         @RequestParam(required = false) String currentPassword,
                         @RequestParam(required = false) String newPassword,
                         @RequestParam(required = false) String confPassword,
                         Model model) {

    try {
      adminService.updateBasicData(id, adminUpdated);

      if (newPassword != null && !newPassword.isBlank()) {
        adminService.updatePassword(id, currentPassword, newPassword, confPassword);
      }

      return "redirect:/admin/usuarios";

    } catch (RuntimeException e) {
      model.addAttribute("errorMessage", e.getMessage());
      model.addAttribute("admin", adminUpdated);
      return "edit_adm";
    }
  }


  @PostMapping("/usuarios")
  public String saveAdmin(@ModelAttribute("admin") @Valid Admin admin,
                          BindingResult result,
                          Model model) {
    if (result.hasErrors()) {
      return "admin_form";
    }

    try {
      adminService.register(admin, admin.getConfPassword());
    } catch (IllegalArgumentException e) {
      model.addAttribute("errorMessage", e.getMessage());
      return "admin_form";
    }

    return "redirect:/admin/usuarios";
  }


  @PostMapping("/register")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> register(@RequestBody Admin admin) {
    try {
      Admin newAdmin = adminService.register(admin, admin.getConfPassword());
      return ResponseEntity.ok(Map.of("admin", newAdmin));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }



  @PutMapping("/{id}/status")
  @ResponseBody
  public ResponseEntity<Admin> updateStatus(@PathVariable Long id, @RequestParam String status) {
    Admin admin = adminRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    if(admin.getStatus() == Status.ATIVO){
      admin.setStatus(Status.INATIVO);
    }
    else {
      admin.setStatus(Status.ATIVO);
    }

    Admin updated = adminRepository.save(admin);
    return ResponseEntity.ok(updated);
  }
}
