package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.DTO.RouteDTO;
import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.service.RouteService;
import com.ja.chegou.ja_chegou.service.RouteServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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


    @GetMapping("/closest")
    public ResponseEntity<List<RouteDTO>> getClosestRoutes(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(required = false, name = "logradouro") String logradouro) {

        List<Route> closest = service.findClosestRoutes(lat, lon);

        if (closest == null || closest.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<RouteDTO> routes = closest.stream()
                .map(r -> {
                    RouteDTO dto = new RouteDTO(r);
                    boolean passaNaRua = false;
                    try {
                        passaNaRua = ((RouteServiceImpl) service)
                                .rotaPassaNaRuaDoCliente(r, lat, lon, logradouro);
                    } catch (Exception e) {
                        System.err.println("Erro ao verificar rua: " + e.getMessage());
                    }
                    dto.setPassaNaRua(passaNaRua);
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(routes);
    }


    public record RouteMapDTO(Long id, String name, String originName, List<double[]> coordinates) {

        public static RouteMapDTO from(Route r) {
            double originLat = 0.0;
            double originLng = 0.0;
            String originName = "Origem desconhecida";

            try {
                if (r.getOrigin() != null) {
                    originLat = r.getOrigin().getLatitude();
                    originLng = r.getOrigin().getLongitude();
                    originName = r.getOrigin().getName();
                } else {
                    System.err.println("Rota " + r.getId() + " sem DistributionCenter (origem).");
                }
            } catch (Exception e) {
                System.err.println("Erro ao obter origem da rota " + r.getId() + ": " + e.getMessage());
            }

            double destLat = r.getDestinationLatitude() != null ? r.getDestinationLatitude() : 0.0;
            double destLng = r.getDestinationLongitude() != null ? r.getDestinationLongitude() : 0.0;

            if (originLat == 0.0 && originLng == 0.0 && destLat == 0.0 && destLng == 0.0) {
                return new RouteMapDTO(r.getId(), r.getDestinationAddress(), originName, List.of());
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

                return new RouteMapDTO(r.getId(), r.getDestinationAddress(), originName, coords);

            } catch (Exception ex) {
                System.err.println("Falha ao gerar rota OSRM para " + r.getDestinationAddress() + ": " + ex.getMessage());
                return new RouteMapDTO(r.getId(), r.getDestinationAddress(), originName, List.of());
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        try {
            Route route = service.findById(id);
            if (route == null) {
                return ResponseEntity.notFound().build();
            }

            if (route.getCoordinates() == null || route.getCoordinates().isEmpty()) {

                double originLat = route.getOriginLatitude();
                double originLng = route.getOriginLongitude();
                double destLat = route.getDestinationLatitude();
                double destLng = route.getDestinationLongitude();

                String url = String.format(
                        Locale.US,
                        "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                        originLng, originLat, destLng, destLat
                );

                RestTemplate rest = new RestTemplate();
                Map<?, ?> resp = rest.getForObject(url, Map.class);

                if (resp != null && "Ok".equals(resp.get("code"))) {
                    List<?> routesList = (List<?>) resp.get("routes");
                    Map<?, ?> firstRoute = (Map<?, ?>) routesList.get(0);
                    Map<?, ?> geometry = (Map<?, ?>) firstRoute.get("geometry");

                    List<List<Double>> coords = (List<List<Double>>) geometry.get("coordinates");

                    List<Route.Coordenada> coordList = new ArrayList<>();
                    for (List<Double> c : coords) {
                        coordList.add(new Route.Coordenada(c.get(1), c.get(0)));
                    }

                    route.setCoordinates(coordList);
                }
            }

            return ResponseEntity.ok(route);

        } catch (Exception e) {
            System.err.println("Erro ao buscar rota por ID " + id + ": " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


}
