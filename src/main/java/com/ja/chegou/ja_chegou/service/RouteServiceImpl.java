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
     if(route.getDestinationLatitude() == null ||
        route.getDestinationLongitude() == null ||
        route.getDistanceKm() == null || route.getDistanceKm() <= 0 ||
        route.getDurationMin() == null || route.getDurationMin() <= 0) {
         throw new IllegalArgumentException("Dados de rota invalidos");
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
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rota " + id + " não encontrada"));
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

        // 🔹 Filtra apenas rotas válidas
        allRoutes = allRoutes.stream()
                .filter(r -> r.getDestinationLatitude() != null && r.getDestinationLongitude() != null)
                .collect(Collectors.toList());

        if (allRoutes.isEmpty()) {
            return List.of();
        }

        allRoutes.forEach(r -> {
            double menorDist = Double.MAX_VALUE;

            try {
                // 🔧 Corrige origem nula usando o DistributionCenter vinculado
                if ((r.getOriginLatitude() == null || r.getOriginLongitude() == null) && r.getOrigin() != null) {
                    if (r.getOrigin().getLatitude() != null && r.getOrigin().getLongitude() != null) {
                        r.setOriginLatitude(r.getOrigin().getLatitude());
                        r.setOriginLongitude(r.getOrigin().getLongitude());
                        System.out.println("✅ Corrigida origem da rota " + r.getId() +
                                " para (" + r.getOriginLatitude() + ", " + r.getOriginLongitude() + ")");
                    }
                }

                // ✅ Buscar coordenadas via OSRM se ainda não estiverem carregadas
                if (r.getCoordinates() == null || r.getCoordinates().isEmpty()) {
                    String url = String.format(
                            Locale.US,
                            "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                            r.getOriginLongitude(), r.getOriginLatitude(),
                            r.getDestinationLongitude(), r.getDestinationLatitude()
                    );

                    System.out.println("Consulta OSRM: " + url);
                    RestTemplate rest = new RestTemplate();
                    Map<?, ?> response = rest.getForObject(url, Map.class);

                    if (response != null && "Ok".equals(response.get("code"))) {
                        List<?> routesList = (List<?>) response.get("routes");
                        if (routesList != null && !routesList.isEmpty()) {
                            Map<?, ?> firstRoute = (Map<?, ?>) routesList.get(0);
                            Map<?, ?> geometry = (Map<?, ?>) firstRoute.get("geometry");

                            if (geometry != null && geometry.containsKey("coordinates")) {
                                List<List<Double>> coords = (List<List<Double>>) geometry.get("coordinates");

                                List<Route.Coordenada> coordList = new ArrayList<>();
                                for (List<Double> coord : coords) {
                                    // ⚠️ ordem no OSRM: [lon, lat]
                                    coordList.add(new Route.Coordenada(coord.get(1), coord.get(0)));
                                }

                                r.setCoordinates(coordList);
                                System.out.println("→ Coordenadas carregadas para rota " + r.getId() +
                                        ": " + coordList.size());
                            } else {
                                System.err.println("⚠️ Nenhuma geometria encontrada para rota " + r.getId());
                            }
                        } else {
                            System.err.println("⚠️ Resposta OSRM sem 'routes' para rota " + r.getId());
                        }
                    } else {
                        System.err.println("⚠️ Falha na consulta OSRM para rota " + r.getId());
                    }
                }

                // ✅ Agora calcula distância até o ponto mais próximo da rota
                if (r.getCoordinates() != null && !r.getCoordinates().isEmpty()) {
                    for (Route.Coordenada p : r.getCoordinates()) {
                        double dist = calcularDistancia(userLat, userLon, p.getLat(), p.getLng());
                        if (dist < menorDist) menorDist = dist;
                    }
                } else {
                    // 🔹 Fallback (origem/destino)
                    if (r.getOriginLatitude() != null && r.getOriginLongitude() != null) {
                        double distOrigem = calcularDistancia(userLat, userLon,
                                r.getOriginLatitude(), r.getOriginLongitude());
                        menorDist = Math.min(menorDist, distOrigem);
                    }
                    if (r.getDestinationLatitude() != null && r.getDestinationLongitude() != null) {
                        double distDestino = calcularDistancia(userLat, userLon,
                                r.getDestinationLatitude(), r.getDestinationLongitude());
                        menorDist = Math.min(menorDist, distDestino);
                    }
                }

            } catch (Exception e) {
                System.err.println("Erro ao calcular distância da rota " + r.getId() + ": " + e.getMessage());
            }

            // 🔸 Define distância final
            r.setDistanceToUser(menorDist == Double.MAX_VALUE ? 9999.0 : menorDist);
        });

        // 🔹 Ordena e retorna as 3 mais próximas
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

}
