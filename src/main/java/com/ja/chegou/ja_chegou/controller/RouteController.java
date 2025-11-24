package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.DTO.RouteDTO;
import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.service.RouteService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/public")
    public List<RouteDTO> listPublicRoutes() {

        return service.findAll().stream()
                .map(route -> {
                    RouteDTO dto = new RouteDTO(route);
                    dto.setCoordinatesFromArray(service.loadCoordinates(route.getId()));
                    return dto;
                })
                .toList();
    }

    // ❗ REMOVIDO: GET /closest
    // Agora esse endpoint pertence ao RouteClosestController com GTFS REAL

    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable Long id) {
        try {
            Route route = service.findById(id);
            RouteDTO dto = new RouteDTO(route);
            dto.setCoordinatesFromArray(service.loadCoordinates(route.getId()));

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
