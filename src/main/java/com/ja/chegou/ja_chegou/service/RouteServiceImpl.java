package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.repository.RoutePointRepository;
import com.ja.chegou.ja_chegou.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RoutePointRepository routePointRepository;

    public RouteServiceImpl(RouteRepository routeRepository,
                            RoutePointRepository routePointRepository) {
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;
    }

    /* ============================================
       LISTAR TODAS AS ROTAS
    ============================================ */
    @Override
    public List<Route> findAll() {
        return routeRepository.findAll();
    }


    @Override
    public Route save(Route route) {

        if (route.getCodigoLinhaOlhoVivo() == null || route.getCodigoLinhaOlhoVivo().isBlank()) {
            throw new IllegalArgumentException("O código da linha SPTrans é obrigatório.");
        }


        if (route.getDistanceKm() == null) {
            route.setDistanceKm(0.0);
        }

        return routeRepository.save(route);
    }


    @Override
    public void delete(Long id) {
        routeRepository.deleteById(id);
    }


    @Override
    public Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rota não encontrada"));
    }


    @Override
    public List<double[]> loadCoordinates(Long routeId) {
        return routePointRepository.findByRouteIdOrderBySequenceAsc(routeId)
                .stream()
                .map(p -> new double[]{p.getLatitude(), p.getLongitude()})
                .toList();
    }


    @Override
    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }


    @Override
    public List<Route> findClosestRoutes(double userLat, double userLon) {

        List<Route> allRoutes = routeRepository.findAll();

        for (Route r : allRoutes) {

            List<double[]> coords = loadCoordinates(r.getId());

            if (coords.isEmpty()) {
                r.setDistanceToUser(Double.MAX_VALUE);
                continue;
            }

            double menor = Double.MAX_VALUE;

            for (double[] c : coords) {
                double d = calcularDistancia(userLat, userLon, c[0], c[1]);
                if (d < menor) menor = d;
            }

            r.setDistanceToUser(menor);
        }

        return allRoutes.stream()
                .sorted(Comparator.comparingDouble(Route::getDistanceToUser))
                .limit(3)
                .toList();
    }
}
