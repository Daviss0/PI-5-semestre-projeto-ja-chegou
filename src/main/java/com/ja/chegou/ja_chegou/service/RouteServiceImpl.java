package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.repository.RouteRepository;
import com.ja.chegou.ja_chegou.repository.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService{

    private final RouteRepository routeRepository;
    private final TruckRepository truckRepository;

    public RouteServiceImpl(RouteRepository routeRepository, TruckRepository truckRepository) {
        this.routeRepository = routeRepository;
        this.truckRepository = truckRepository;
    }

    @Override
    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    @Override
    public Route save(Route route) {

        if (route.getOrigin() == null) {
            throw new IllegalArgumentException("A rota deve ter uma central de origem.");
        }

        if (route.getOrigin().getLatitude() == null || route.getOrigin().getLongitude() == null) {
            throw new IllegalArgumentException("A central de origem deve possuir latitude e longitude.");
        }

        route.setOriginLatitude(route.getOrigin().getLatitude());
        route.setOriginLongitude(route.getOrigin().getLongitude());

        if (route.getDestinationLatitude() == null ||
                route.getDestinationLongitude() == null) {

            throw new IllegalArgumentException("A rota deve possuir latitude e longitude de destino.");
        }

        route.setOriginLatitude(Double.parseDouble(route.getOriginLatitude().toString().replace(",", ".")));
        route.setOriginLongitude(Double.parseDouble(route.getOriginLongitude().toString().replace(",", ".")));
        route.setDestinationLatitude(Double.parseDouble(route.getDestinationLatitude().toString().replace(",", ".")));
        route.setDestinationLongitude(Double.parseDouble(route.getDestinationLongitude().toString().replace(",", ".")));


        if (route.getDistanceKm() == null || route.getDistanceKm() <= 0 ||
                route.getDurationMin() == null || route.getDurationMin() <= 0) {

            throw new IllegalArgumentException("Dados de rota inválidos: distância e duração devem ser positivos.");
        }

        return routeRepository.save(route);
    }
    
    @Override
    public void delete(Long id) {
        routeRepository.deleteById(id);
    }

    @Override
    public List<Route> getRoutesByTruck(Long truckId) {
        return routeRepository.findByTruckId(truckId);
    }

    @Override
    public Route findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da rota não pode ser nulo");
        }

        Route r = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rota " + id + " não encontrada"));

        gerarCoordenadasSeNecessario(r);

        return r;
    }


    @Override
   public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Raio da Terra em km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public List<Route> findClosestRoutes(double userLat, double userLon) {
        List<Route> allRoutes = routeRepository.findAll();

        allRoutes = allRoutes.stream()
                .filter(r -> r.getDestinationLatitude() != null && r.getDestinationLongitude() != null)
                .collect(Collectors.toList());

        if (allRoutes.isEmpty()) {
            return List.of();
        }

        allRoutes.forEach(r -> {
            double menorDist = Double.MAX_VALUE;

            try {
                if ((r.getOriginLatitude() == null || r.getOriginLongitude() == null) && r.getOrigin() != null) {
                    if (r.getOrigin().getLatitude() != null && r.getOrigin().getLongitude() != null) {
                        r.setOriginLatitude(r.getOrigin().getLatitude());
                        r.setOriginLongitude(r.getOrigin().getLongitude());
                    }
                }

                gerarCoordenadasSeNecessario(r);

                if (r.getCoordinates() != null && !r.getCoordinates().isEmpty()) {
                    for (Route.Coordenada p : r.getCoordinates()) {
                        double dist = calcularDistancia(userLat, userLon, p.getLat(), p.getLng());
                        if (dist < menorDist) menorDist = dist;
                    }
                } else {
                    if (r.getOriginLatitude() != null && r.getOriginLongitude() != null) {
                        menorDist = Math.min(menorDist,
                                calcularDistancia(userLat, userLon, r.getOriginLatitude(), r.getOriginLongitude()));
                    }
                    if (r.getDestinationLatitude() != null && r.getDestinationLongitude() != null) {
                        menorDist = Math.min(menorDist,
                                calcularDistancia(userLat, userLon, r.getDestinationLatitude(), r.getDestinationLongitude()));
                    }
                }

            } catch (Exception e) {
                System.err.println("Erro na rota " + r.getId() + ": " + e.getMessage());
            }

            r.setDistanceToUser(menorDist == Double.MAX_VALUE ? 9999.0 : menorDist);
        });

        return allRoutes.stream()
                .sorted(Comparator.comparingDouble(Route::getDistanceToUser))
                .limit(3)
                .collect(Collectors.toList());
    }


    public boolean rotaPassaNaRuaDoCliente(Route route, double clientLat, double clientLon, String logradouroCliente) {
        try {
            double latRef = route.getDestinationLatitude();
            double lonRef = route.getDestinationLongitude();

            if (route.getCoordinates() != null && !route.getCoordinates().isEmpty()) {
                double menor = Double.MAX_VALUE;
                for (Route.Coordenada p : route.getCoordinates()) {
                    double d = calcularDistancia(clientLat, clientLon, p.getLat(), p.getLng());
                    if (d < menor) {
                        menor = d;
                        latRef = p.getLat();
                        lonRef = p.getLng();
                    }
                }
            }

            String url = UriComponentsBuilder
                    .fromHttpUrl("https://nominatim.openstreetmap.org/reverse")
                    .queryParam("format", "jsonv2")
                    .queryParam("lat", latRef)
                    .queryParam("lon", lonRef)
                    .toUriString();

            RestTemplate rest = new RestTemplate();
            Map<?, ?> resp = rest.getForObject(url, Map.class);

            if (resp == null || !resp.containsKey("address")) return false;

            Map<?, ?> address = (Map<?, ?>) resp.get("address");
            String road = (String) address.get("road");

            return road != null && road.equalsIgnoreCase(logradouroCliente);
        } catch (Exception e) {
            System.err.println("Erro no reverse geocoding: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void gerarCoordenadasSeNecessario(Route r) {

        if (r == null) return;

        if (r.getCoordinates() != null && !r.getCoordinates().isEmpty()) {
            return;
        }

        try {
            Double originLat = r.getOriginLatitude();
            Double originLng = r.getOriginLongitude();
            Double destLat = r.getDestinationLatitude();
            Double destLng = r.getDestinationLongitude();

            if (originLat == null || originLng == null || destLat == null || destLng == null) {
                System.err.println("⚠️ Rota " + r.getId() + " sem coordenadas válidas.");
                return;
            }

            String url = String.format(
                    Locale.US,
                    "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                    originLng, originLat, destLng, destLat
            );

            RestTemplate rest = new RestTemplate();
            Map<?, ?> response = rest.getForObject(url, Map.class);

            if (response != null && "Ok".equals(response.get("code"))) {
                List<?> routesList = (List<?>) response.get("routes");
                Map<?, ?> firstRoute = (Map<?, ?>) routesList.get(0);
                Map<?, ?> geometry = (Map<?, ?>) firstRoute.get("geometry");

                List<List<Double>> coords = (List<List<Double>>) geometry.get("coordinates");

                List<Route.Coordenada> coordList = new ArrayList<>();
                for (List<Double> c : coords) {
                    coordList.add(new Route.Coordenada(c.get(1), c.get(0))); // lat, lng
                }

                r.setCoordinates(coordList);

                System.out.println("Coordenadas geradas para rota " + r.getId() +
                        " (" + coordList.size() + " pontos)");

            } else {
                System.err.println("Falha na consulta OSRM para rota " + r.getId());
            }

        } catch (Exception e) {
            System.err.println("Erro ao gerar coordenadas: " + e.getMessage());
        }
    }
}
