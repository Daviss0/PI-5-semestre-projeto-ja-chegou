package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.service.DistributionCenterService;
import com.ja.chegou.ja_chegou.service.RouteService;
import com.ja.chegou.ja_chegou.service.TruckService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/routes")
public class RouteViewController {

    private final RouteService routeService;
    private final DistributionCenterService centerService;
    private final TruckService truckService;

    public RouteViewController(RouteService routeService,
                               DistributionCenterService centerService,
                               TruckService truckService) {
        this.routeService = routeService;
        this.centerService = centerService;
        this.truckService = truckService;
    }

    @GetMapping("/manage")
    public String manageRoutes(Model model) {
        model.addAttribute("centers", centerService.findAll());
        model.addAttribute("routes", routeService.findAll());
        model.addAttribute("route", new Route());
        return "manage_routes";
    }

    @PostMapping("/manage")
    public String saveRoute(@ModelAttribute Route route) {
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

    @GetMapping("/monitoring")
    public String generalMonitoring(Model model) {
        model.addAttribute("routes", routeService.findAll());
        return "general_monitoring";
    }
}
