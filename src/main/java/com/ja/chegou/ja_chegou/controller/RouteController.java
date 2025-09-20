package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService service;

    public RouteController(RouteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Route> listAll() {
        return service.findAll();
    }

    @PostMapping
    public Route create(@RequestBody Route route) {
        return service.save(route);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
