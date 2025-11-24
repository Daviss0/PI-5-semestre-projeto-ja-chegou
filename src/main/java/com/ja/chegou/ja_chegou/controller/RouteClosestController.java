// ============================
// RouteClosestController.java — FINAL
// ============================

package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.gtfs.GTFSService;
import com.ja.chegou.ja_chegou.gtfs.GTFSLoader;
import com.ja.chegou.ja_chegou.service.OlhoVivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/routes")
public class RouteClosestController {

    private final GTFSService gtfsService;

    public RouteClosestController(GTFSService gtfsService) {
        this.gtfsService = gtfsService;
    }

    @GetMapping("/closest")
    public ResponseEntity<?> getClosest(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        return ResponseEntity.ok(gtfsService.findClosestRoutes(lat, lon));
    }

    // ROTA POR SHORTNAME — compatível com o App
    @GetMapping("/byShortName/{shortName}")
    public ResponseEntity<?> getByShortName(@PathVariable String shortName) {

        Optional<GTFSLoader.RouteGTFS> routeOpt =
                gtfsService.loader.routes.values()
                        .stream()
                        .filter(r -> r.shortName().equalsIgnoreCase(shortName))
                        .findFirst();

        if (routeOpt.isEmpty())
            return ResponseEntity.notFound().build();

        GTFSLoader.RouteGTFS route = routeOpt.get();

        List<double[]> shape = new ArrayList<>();

        gtfsService.loader.trips.values()
                .stream()
                .filter(t -> t.route_id().equals(route.route_id()))
                .findFirst()
                .ifPresent(trip -> {
                    List<GTFSLoader.ShapePoint> points =
                            gtfsService.loader.shapes.get(trip.shape_id());

                    if (points != null) {
                        for (GTFSLoader.ShapePoint p : points) {
                            shape.add(new double[]{p.lat(), p.lon()});
                        }
                    }
                });

        Map<String, Object> json = Map.of(
                "routeId", route.route_id(),
                "shortName", route.shortName(),
                "longName", route.longName(),
                "distanceToUser", 0,
                "shape", shape
        );

        return ResponseEntity.ok(json);
    }
}
