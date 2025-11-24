package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.DistributionCenter;
import com.ja.chegou.ja_chegou.service.DistributionCenterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/centers")
public class DistributionCenterViewController {

    private final DistributionCenterService centerService;

    public DistributionCenterViewController(DistributionCenterService centerService) {
        this.centerService = centerService;
    }

    @GetMapping
    public String manageCenters(Model model) {
        model.addAttribute("centers", centerService.findAll());
        model.addAttribute("center", new DistributionCenter());
        return "manage_centers";
    }

    @PostMapping
    public String saveCenters(@ModelAttribute("center") DistributionCenter center) {
        centerService.save(center);
        return "redirect:/centers";
    }

    @GetMapping("/{id}/delete")
    public String deleteCenter(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            centerService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Central excluída com sucesso!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/centers";
    }

}
