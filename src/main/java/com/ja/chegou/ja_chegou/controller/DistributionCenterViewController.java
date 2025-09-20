package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.DistributionCenter;
import com.ja.chegou.ja_chegou.service.DistributionCenterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/centers")
public class DistributionCenterViewController {

    private final DistributionCenterService centerService;

    public DistributionCenterViewController(DistributionCenterService centerService) {
        this.centerService = centerService;
    }

    // Tela de gerenciamento
    @GetMapping
    public String manageCenters(Model model) {
        model.addAttribute("centers", centerService.findAll());
        model.addAttribute("center", new DistributionCenter());
        return "manage_centers";
    }

    // Salvar nova central
    @PostMapping
    public String saveCenters(@ModelAttribute("center") DistributionCenter center) {
        centerService.save(center);
        return "redirect:/centers";
    }

    // Excluir central
    @GetMapping("/{id}/delete")
    public String deleteCenter(@PathVariable Long id) {
        centerService.delete(id);
        return "redirect:/centers";
    }
}
