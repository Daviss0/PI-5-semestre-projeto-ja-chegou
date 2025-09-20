package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.service.DistributionCenterService;
import com.ja.chegou.ja_chegou.service.RouteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/routes")
public class RouteViewController {

    private final RouteService routeService;
    private final DistributionCenterService centerService;

    public RouteViewController(RouteService routeService, DistributionCenterService centerService) {
        this.routeService = routeService;
        this.centerService = centerService;
    }

    // Tela de gerenciamento
    @GetMapping("/manage")
    public String manageRoutes(Model model) {
        model.addAttribute("centers", centerService.findAll());
        model.addAttribute("routes", routeService.findAll());
        model.addAttribute("route", new Route());
        return "manage_routes";
    }

    @PostMapping
    public String saveRoute(@ModelAttribute("route") Route route) {
        routeService.save(route);
        return "redirect:/routes/manage";
    }

    @GetMapping("/{id}/delete")
    public String deleteRoute(@PathVariable Long id) {
        routeService.delete(id);
        return "redirect:/routes/manage";
    }

    @GetMapping("/menu")
    public String menuRoutes() {
        return "menu_routes";
    }
}
