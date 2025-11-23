package com.ja.chegou.ja_chegou.gtfs;

import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class GTFSLoader {

    private static final String GTFS_FOLDER = "gtfs/";

    // 🔥 LINHAS SELECIONADAS (APENAS ESSAS SERÃO CARREGADAS)
    private static final Set<String> SELECTED_LINES = Set.of(
            "546L-10", "675P-10", "N634-11", "607A-10",
            "627M-10", "745M-10", "5129-10", "6030-10",
            "6062-51", "6091-21", "6091-51", "N631-11"
    );

    // 🔥 DADOS EM MEMÓRIA
    public Map<String, RouteGTFS> routes = new HashMap<>();
    public Map<String, TripGTFS> trips = new HashMap<>();
    public Map<String, List<ShapePoint>> shapes = new HashMap<>();

    @PostConstruct
    public void loadGTFS() {
        try {
            System.out.println("\n📦 Carregando GTFS filtrado (somente 12 linhas selecionadas)...");

            // 1. Carregar route_id dos selectedLines
            Set<String> allowedRouteIds = loadAllowedRouteIds();

            // 2. Carregar trips e descobrir quais shapes pertencem às rotas selecionadas
            Set<String> allowedShapeIds = loadTripsAndCollectShapes(allowedRouteIds);

            // 3. Carregar shapes.txt apenas desses shape_id
            loadShapes(allowedShapeIds);

            System.out.println("\n✔ GTFS carregado com sucesso!");
            System.out.println("Routes carregadas: " + routes.size());
            System.out.println("Trips carregadas: " + trips.size());
            System.out.println("Shapes carregados: " + shapes.size());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar GTFS: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // 1️⃣ CARREGA APENAS ROUTES.txt das linhas selecionadas
    // =========================================================
    private Set<String> loadAllowedRouteIds() throws Exception {
        Set<String> allowedRouteIds = new HashSet<>();

        try (InputStream is = getFile("routes.txt");
             CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String[] header = reader.readNext();
            Map<String, Integer> idx = indexMap(header);

            String[] row;
            while ((row = reader.readNext()) != null) {
                String shortName = row[idx.get("route_short_name")].trim();
                if (!SELECTED_LINES.contains(shortName)) continue;

                String routeId = row[idx.get("route_id")].trim();
                String longName = row[idx.get("route_long_name")].trim();

                routes.put(routeId, new RouteGTFS(routeId, shortName, longName));
                allowedRouteIds.add(routeId);
            }
        }

        System.out.println("➡ route_id selecionados: " + allowedRouteIds);

        return allowedRouteIds;
    }

    // =========================================================
    // 2️⃣ CARREGA trips.txt E COLETA OS shape_id NECESSÁRIOS
    // =========================================================
    private Set<String> loadTripsAndCollectShapes(Set<String> allowedRouteIds) throws Exception {
        Set<String> allowedShapeIds = new HashSet<>();

        try (InputStream is = getFile("trips.txt");
             CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String[] header = reader.readNext();
            Map<String, Integer> idx = indexMap(header);

            String[] row;
            while ((row = reader.readNext()) != null) {
                String routeId = row[idx.get("route_id")].trim();

                if (!allowedRouteIds.contains(routeId)) continue;

                String tripId = row[idx.get("trip_id")].trim();
                String shapeId = row[idx.get("shape_id")].trim();

                trips.put(tripId, new TripGTFS(tripId, routeId, shapeId));
                allowedShapeIds.add(shapeId);
            }
        }

        System.out.println("➡ shape_id necessários: " + allowedShapeIds);

        return allowedShapeIds;
    }

    // =========================================================
    // 3️⃣ CARREGA shapes.txt MAS SOMENTE PARA shape_id SELECIONADOS
    // =========================================================
    private void loadShapes(Set<String> allowedShapeIds) throws Exception {

        try (InputStream is = getFile("shapes.txt");
             CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String[] header = reader.readNext();
            Map<String, Integer> idx = indexMap(header);

            String[] row;
            while ((row = reader.readNext()) != null) {
                String shapeId = row[idx.get("shape_id")].trim();

                if (!allowedShapeIds.contains(shapeId))
                    continue;

                double lat = Double.parseDouble(row[idx.get("shape_pt_lat")].trim());
                double lon = Double.parseDouble(row[idx.get("shape_pt_lon")].trim());
                int seq = Integer.parseInt(row[idx.get("shape_pt_sequence")].trim());

                shapes.computeIfAbsent(shapeId, k -> new ArrayList<>())
                        .add(new ShapePoint(shapeId, lat, lon, seq));
            }
        }

        // ordena por sequência
        shapes.values().forEach(list ->
                list.sort(Comparator.comparingInt(ShapePoint::sequence))
        );
    }

    // =========================================================
    // UTILITÁRIOS
    // =========================================================

    private InputStream getFile(String name) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(GTFS_FOLDER + name);
        if (is == null) {
            throw new RuntimeException("Arquivo GTFS não encontrado: " + name);
        }
        return is;
    }

    private Map<String, Integer> indexMap(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            map.put(header[i].replace("\"", "").trim(), i);
        }
        return map;
    }

    // =========================================================
    // RECORDS / MODELOS
    // =========================================================

    public record RouteGTFS(String route_id, String shortName, String longName) {}

    public record TripGTFS(String trip_id, String route_id, String shape_id) {}

    public record ShapePoint(String shape_id, double lat, double lon, int sequence) {}
}
