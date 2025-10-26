package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Driver;
import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.entity.Truck;
import com.ja.chegou.ja_chegou.repository.DriverRepository;
import com.ja.chegou.ja_chegou.repository.RouteRepository;
import com.ja.chegou.ja_chegou.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TruckServiceImpl implements TruckService{

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RouteService routeService;
    @Autowired
    private RouteRepository routeRepository;

    private final Random random = new Random();

    private final ConcurrentMap<Long, Integer> progressMap = new ConcurrentHashMap<>();

    @Override
    public List<Truck> findAll() {
        return truckRepository.findAll();
    }

    @Override
    public Truck findById(Long id) {
        return truckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Caminhão não encontrado"));
    }

    @Override
    public Truck save(Truck truck) {
        if(truckRepository.existsByPlate(truck.getPlate())) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }

        if(!isCapacityValid(truck)) {
            throw new IllegalArgumentException("Capacidade do caminhão não é compativel com a(s) rota(s) associada(s)");
        }
        return truckRepository.save(truck);
    }


    @Override
    public Truck save(Truck truck, Driver driver) {
        if(truckRepository.existsByPlate(truck.getPlate())) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }
        if(driver.getTruck() != null) {
          throw new IllegalArgumentException("Motorista já esta atribuído a outro caminhão");
        }
        return truckRepository.save(truck);
    }

    @Override
    public Truck update(Truck truck) {
        Truck existingTruck = truckRepository.findById(truck.getId())
                .orElseThrow(() -> new IllegalArgumentException("Caminhão não encontrado"));

        if(existingTruck.getPlate().equals(truck.getPlate()) &&
        truckRepository.existsByPlate(truck.getPlate())) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }

        if(!isCapacityValid(truck)) {
            throw new IllegalArgumentException("Capacidade do caminhão não é compativel com a(s) rota(s) associada(s)");
        }

        truck.setDriver(existingTruck.getDriver());
        return truckRepository.save(truck);
    }

    @Override
    public void delete(Long id) {
        Truck truck = findById(id);

        if (truck.getDriver() != null) {
            throw new IllegalArgumentException("Não é possível excluir: caminhão possui motorista atribuído");
        }

        List<Route> routes = routeService.getRoutesByTruck(truck.getId());
        if (!routes.isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir: caminhão vinculado a rotas ativas");
        }

        truckRepository.delete(truck);
    }

    @Override
    public void assignDriver(Long truckId, Long driverId) {
        Truck truck = findById(truckId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado: " + driverId));

        if(driver.getTruck() != null) {
            throw new IllegalArgumentException("Motorista já está atribuído a outro caminhão");
        }

        truck.setDriver(driver);
        truckRepository.save(truck);
    }


    @Override
    public void unassignDriver(Long truckId) {
        Truck truck = findById(truckId);
        truck.setDriver(null);
        truckRepository.save(truck);
    }

    private boolean isCapacityValid(Truck truck) {
        return routeService.getRoutesByTruck(truck.getId()).stream()
                .allMatch(route -> truck.getCapacity() >= route.getRequiredCapacity());
    }

    @Override
    public Map<String, String> getTruckByDriver() {
        List<Truck> trucks = truckRepository.findAll();
        Map<String, String> result = new HashMap<>();

        for(Truck truck : trucks) {
            String driverName = truck.getDriver() != null ? truck.getDriver().getName() : "Sem motorista";
            result.put(truck.getPlate(), driverName);
        }
        return result;
    }

    @Override
    public Map<String, Long> getTruckStatusSummary() {
        long actives = truckRepository.countByStatusTrue();
        long inactives = truckRepository.countByStatusFalse();
        Map<String, Long> result = new HashMap<>();
        result.put("Ativos", actives);
        result.put("Inativos", inactives);
        return result;
    }

    @Override
    public Map<String, Double> getTotalCapacityPerRoute() {
        List<Route> routes = routeRepository.findAll();
        Map<String, Double> result = new HashMap<>();

        for (Route route : routes) {
            Truck truck = route.getTruck();
            double capacity = truck != null ? truck.getCapacity() : 0;
            result.put(route.getDestinationAddress(), capacity);
        }

        return result;
    }

    @Scheduled(fixedRate = 3000)
    public void simulateTruckMoviment() {
        List<Truck> trucks = truckRepository.findAll();

        if (trucks.isEmpty()) return;

        for (Truck t : trucks) {
            if (t.getRoute() == null) {
                List<Route> routes = routeRepository.findAll();
                if (!routes.isEmpty()) {
                    t.setRoute(routes.get(0));
                    truckRepository.save(t);
                    System.out.println("Caminhão " + t.getPlate() + " associado à rota " + routes.get(0).getDestinationAddress());
                } else {
                    System.out.println("Nenhuma rota disponível para o caminhão " + t.getPlate());
                    continue;
                }
            }

            Route r = t.getRoute();

            if (r.getOrigin() == null) {
                System.out.println("Caminhão " + t.getPlate() + " tem rota sem origem. Pulando...");
                continue;
            }

            // 🔹 2. Recupera coordenadas da rota
            double originLat = r.getOrigin().getLatitude();
            double originLng = r.getOrigin().getLongitude();
            double destLat = r.getDestinationLatitude();
            double destLng = r.getDestinationLongitude();

            try {
                // 🔹 3. Chama o OSRM (rota real)
                String url = String.format(
                        Locale.US, // garante ponto decimal
                        "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                        originLng, originLat, destLng, destLat
                );

                RestTemplate rest = new RestTemplate();
                Map<String, Object> resp = rest.getForObject(url, Map.class);
                Map<String, Object> route = (Map<String, Object>) ((List<?>) resp.get("routes")).get(0);
                Map<String, Object> geometry = (Map<String, Object>) route.get("geometry");

                @SuppressWarnings("unchecked")
                List<List<Double>> coordsRaw = (List<List<Double>>) geometry.get("coordinates");
                if (coordsRaw == null || coordsRaw.isEmpty()) continue;

                int index = progressMap.getOrDefault(t.getId(), 0);
                if (index >= coordsRaw.size()) index = 0; // volta ao início da rota

                List<Double> point = coordsRaw.get(index);
                t.setCurrentLatitude(point.get(1));
                t.setCurrentLongitude(point.get(0));
                truckRepository.save(t);

                progressMap.put(t.getId(), index + 1);

                System.out.printf("Caminhão %s movido para (%.6f, %.6f) [%d/%d]%n",
                        t.getPlate(), point.get(1), point.get(0), index, coordsRaw.size());

            } catch (Exception e) {
                System.err.println("Falha ao atualizar caminhão " + t.getPlate() + ": " + e.getMessage());
            }
        }
    }

}
