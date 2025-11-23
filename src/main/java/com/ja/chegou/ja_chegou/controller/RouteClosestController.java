package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.gtfs.GTFSService;
import com.ja.chegou.ja_chegou.gtfs.dto.GTFSRouteDTO;
import com.ja.chegou.ja_chegou.service.OlhoVivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteClosestController {

    private final GTFSService gtfsService;
    private final OlhoVivoService olhoVivoService;

    public RouteClosestController(GTFSService gtfsService, OlhoVivoService olhoVivoService) {
        this.gtfsService = gtfsService;
        this.olhoVivoService = olhoVivoService;
    }

    // mapa linha GTFS → CLs reais da SPTrans
    private static final Map<String, List<String>> LINE_TO_CL = Map.ofEntries(
            Map.entry("675P-10", List.of("275", "33043")),
            Map.entry("N634-11", List.of("2399", "35167")),
            Map.entry("607A-10", List.of("2139", "34907")),
            Map.entry("627M-10", List.of("2597", "35365")),
            Map.entry("745M-10", List.of("782", "33550")),
            Map.entry("5129-10", List.of("1281", "34049")),
            Map.entry("6030-10", List.of("1142", "33910")),
            Map.entry("6062-51", List.of("1739", "34507")),
            Map.entry("6091-21", List.of("1176", "33944")),
            Map.entry("6091-51", List.of("1691", "34459")),
            Map.entry("N631-11", List.of("2396", "35164")),
            Map.entry("546L-10", List.of("1285", "34053"))
    );

    @GetMapping("/closest")
    public ResponseEntity<?> getClosestRoutes(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        // 1️⃣ Rotas mais próximas (GTFS)
        List<GTFSRouteDTO> routes = gtfsService.findClosestRoutes(lat, lon);

        // 2️⃣ Montar estrutura para adicionar ônibus
        List<Map<String, Object>> result = new ArrayList<>();

        for (GTFSRouteDTO route : routes) {

            // pegar shortName (ex: 675P-10)
            String shortName = route.getShortName();

            // achar CLs da linha real
            List<String> cls = LINE_TO_CL.getOrDefault(shortName, List.of());

            // consultar ônibus reais
            List<Object> buses = new ArrayList<>();

            for (String cl : cls) {
                Map<String, Object> info = olhoVivoService.buscarVeiculosPorCodigoComercial(cl);

                if (info != null && !info.containsKey("erro") && info.get("vs") != null) {
                    buses.addAll((List<?>) info.get("vs"));
                }
            }

            // montar retorno completo
            Map<String, Object> json = new HashMap<>();
            json.put("routeId", route.getRouteId());
            json.put("shortName", route.getShortName());
            json.put("longName", route.getLongName());
            json.put("distanceToUser", route.getDistanceToUser());
            json.put("shape", route.getShape());
            json.put("buses", buses);

            result.add(json);
        }

        return ResponseEntity.ok(result);
    }
}
