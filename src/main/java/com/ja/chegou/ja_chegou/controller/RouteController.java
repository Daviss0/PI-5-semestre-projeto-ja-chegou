package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.service.RouteService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<RouteMapDTO> listPublicRoutes() {
        return service.findAll().stream()
                .map(RouteMapDTO::from)
                .collect(Collectors.toList());
    }

    public record RouteMapDTO(Long id, String name, List<double[]> coordinates) {

        public static RouteMapDTO from(Route r) {
            double originLat = 0.0;
            double originLng = 0.0;

            try {
                if (r.getOrigin() != null) {
                    originLat = r.getOrigin().getLatitude();
                    originLng = r.getOrigin().getLongitude();
                } else {
                    System.err.println("⚠️  Rota " + r.getId() + " sem DistributionCenter (origem).");
                }
            } catch (Exception e) {
                System.err.println("⚠️  Erro ao obter origem da rota " + r.getId() + ": " + e.getMessage());
            }

            double destLat = r.getDestinationLatitude() != null ? r.getDestinationLatitude() : 0.0;
            double destLng = r.getDestinationLongitude() != null ? r.getDestinationLongitude() : 0.0;

            // se alguma coordenada for 0.0, evita chamar o OSRM
            if (originLat == 0.0 && originLng == 0.0 && destLat == 0.0 && destLng == 0.0) {
                return new RouteMapDTO(r.getId(), r.getDestinationAddress(), List.of());
            }

            try {
                RestTemplate rest = new RestTemplate();
                String url = String.format(
                        Locale.US,
                        "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                        originLng, originLat, destLng, destLat
                );

                Map<String, Object> resp = rest.getForObject(url, Map.class);
                Map<String, Object> route = (Map<String, Object>) ((List<?>) resp.get("routes")).get(0);
                Map<String, Object> geometry = (Map<String, Object>) route.get("geometry");

                @SuppressWarnings("unchecked")
                List<List<Double>> coordsRaw = (List<List<Double>>) geometry.get("coordinates");

                List<double[]> coords = coordsRaw.stream()
                        .map(c -> new double[]{c.get(1), c.get(0)})
                        .toList();

                return new RouteMapDTO(r.getId(), r.getDestinationAddress(), coords);

            } catch (Exception ex) {
                System.err.println("Falha ao gerar rota OSRM para " + r.getDestinationAddress() + ": " + ex.getMessage());
                return new RouteMapDTO(r.getId(), r.getDestinationAddress(), List.of());
            }
        }
    }
}