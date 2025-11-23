package com.ja.chegou.ja_chegou.gtfs;

import com.ja.chegou.ja_chegou.gtfs.dto.GTFSRouteDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GTFSService {

    private final GTFSLoader loader;

    public GTFSService(GTFSLoader loader) {
        this.loader = loader;
    }

    // ========================================================
    // 🔍 ENCONTRAR AS 4 ROTAS MAIS PRÓXIMAS DO PONTO (lat/lon)
    // ========================================================
    public List<GTFSRouteDTO> findClosestRoutes(double userLat, double userLon) {

        System.out.println("➡ Procurando rotas mais próximas para: " + userLat + ", " + userLon);

        List<GTFSRouteDTO> result = new ArrayList<>();

        for (GTFSLoader.TripGTFS trip : loader.trips.values()) {

            String routeId = trip.route_id();
            String shapeId = trip.shape_id();

            // ignora rotas sem shape
            List<GTFSLoader.ShapePoint> shapeList = loader.shapes.get(shapeId);
            if (shapeList == null || shapeList.isEmpty()) continue;

            // calcula distância mínima entre o usuário e qualquer ponto do shape
            double minDist = calculateDistanceToShape(userLat, userLon, shapeList);

            GTFSLoader.RouteGTFS route = loader.routes.get(routeId);
            if (route == null) continue;

            // monta DTO com shape completo (convertido para double[])
            List<double[]> shapeCoordinates = shapeList.stream()
                    .sorted(Comparator.comparingInt(GTFSLoader.ShapePoint::sequence))
                    .map(s -> new double[]{s.lat(), s.lon()})
                    .collect(Collectors.toList());

            result.add(new GTFSRouteDTO(
                    route.route_id(),
                    route.shortName(),
                    route.longName(),
                    minDist,
                    shapeCoordinates
            ));
        }

        // Ordena pelas mais próximas e pega só 4
        return result.stream()
                .sorted(Comparator.comparingDouble(GTFSRouteDTO::getDistanceToUser))
                .toList();
    }

    // ========================================================
    // 🔢 DISTÂNCIA ENTRE O USUÁRIO E UMA ROTA (shape)
    // ========================================================
    private double calculateDistanceToShape(
            double userLat,
            double userLon,
            List<GTFSLoader.ShapePoint> shapePoints
    ) {
        double min = Double.MAX_VALUE;

        for (GTFSLoader.ShapePoint p : shapePoints) {
            double d = haversine(userLat, userLon, p.lat(), p.lon());
            if (d < min) min = d;
        }

        return min;
    }

    // ========================================================
    // 🌍 HAVERSINE — DISTÂNCIA EM KM ENTRE DOIS PONTOS
    // ========================================================
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);

        return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)));
    }
}
