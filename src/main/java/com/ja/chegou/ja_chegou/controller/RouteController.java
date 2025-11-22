package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.DTO.RouteDTO;
import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.service.RouteService;
import com.ja.chegou.ja_chegou.service.RouteServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /** 🔥 Importar rota SPTrans */
    @PostMapping("/import/olhovivo/{codigo}")
    public ResponseEntity<?> importarOlhoVivo(@PathVariable String codigo) {
        try {
            Route route = ((RouteServiceImpl) service).importarRotaSPTrans(codigo);
            return ResponseEntity.ok(route);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }



    /** 🔥 Posição ao vivo simplificada (para o app mobile) */
    @GetMapping("/{codigo}/live/basic")
    public ResponseEntity<?> livePositionSimple(@PathVariable String codigo) {
        Object data = ((RouteServiceImpl) service).livePositionSimple(codigo);
        return ResponseEntity.ok(data);
    }

    /** 🔥 Retornar shape da rota (lista de pontos lat/lng) */
    @GetMapping("/{id}/shape")
    public ResponseEntity<?> getShape(@PathVariable Long id) {

        Route route = service.findById(id);

        if (route.getShape() == null)
            return ResponseEntity.ok(List.of());

        return ResponseEntity.ok(
                route.getShape().stream()
                        .map(c -> Map.of("lat", c.getLat(), "lng", c.getLng()))
                        .toList()
        );
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

    @GetMapping("/closest")
    public ResponseEntity<List<RouteDTO>> getClosestRoutes(
            @RequestParam double lat,
            @RequestParam double lon
    ) {

        List<RouteDTO> result = service.findClosestRoutes(lat, lon).stream()
                .map(route -> {
                    RouteDTO dto = new RouteDTO(route);
                    dto.setCoordinatesFromArray(service.loadCoordinates(route.getId()));
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(result);
    }

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
