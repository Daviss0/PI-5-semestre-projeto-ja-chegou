package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Route;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class OSRMService {

    private final RestTemplate restTemplate;

    public OSRMService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Route.Coordenada> gerarRota(double latA, double lonA, double latB, double lonB) {

        try {
            // IMPORTANTE: Locale.US garante ponto decimal e não vírgula
            String url = String.format(
                    Locale.US,
                    "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                    lonA, latA, lonB, latB
            );

            Map<String, Object> resp = restTemplate.getForObject(url, Map.class);

            List<Route.Coordenada> shape = new ArrayList<>();

            if (resp != null && resp.containsKey("routes")) {

                List<Map<String, Object>> routes = (List<Map<String, Object>>) resp.get("routes");

                if (routes != null && !routes.isEmpty()) {
                    Map<String, Object> geometry =
                            (Map<String, Object>) routes.get(0).get("geometry");

                    List<List<Double>> coords =
                            (List<List<Double>>) geometry.get("coordinates");

                    for (List<Double> c : coords) {
                        double lng = c.get(0);
                        double lat = c.get(1);
                        shape.add(new Route.Coordenada(lat, lng));
                    }
                }
            }

            return shape;

        } catch (Exception e) {
            System.err.println("Erro OSRM: " + e.getMessage());
            return List.of();
        }
    }
}
