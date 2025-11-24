package com.ja.chegou.ja_chegou.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/maps")
public class MapsController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // ============================================================
    // 🔎 SUGESTÕES DE ENDEREÇO (limpas e filtradas)
    // ============================================================
    @GetMapping("/suggestions")
    public ResponseEntity<?> getSuggestions(@RequestParam String q) {
        try {
            String url = "https://nominatim.openstreetmap.org/search"
                    + "?format=json"
                    + "&addressdetails=1"
                    + "&limit=5"
                    + "&countrycodes=br"
                    + "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            List<Map<String, Object>> raw = mapper.readValue(response.body(), List.class);
            List<Map<String, String>> refined = new ArrayList<>();

            for (var item : raw) {
                Map<String, Object> address = (Map<String, Object>) item.get("address");
                if (address == null) continue;

                // ---------------------------------------
                // 🌎 Filtrar APENAS São Paulo (Estado)
                // ---------------------------------------
                String state = (String) address.getOrDefault("state", "");
                if (!state.equalsIgnoreCase("São Paulo")) continue;

                // ---------------------------------------
                // Cidade (city, town ou village)
                // ---------------------------------------
                String city = (String) address.getOrDefault("city", "");
                String town = (String) address.getOrDefault("town", "");
                String village = (String) address.getOrDefault("village", "");

                String localCity = !city.isBlank() ? city :
                        (!town.isBlank() ? town : village);

                // ---------------------------------------
                // Nome / Rua
                // ---------------------------------------
                String road = (String) address.getOrDefault("road", "");
                String name = road;

                // ---------------------------------------
                // Bairro / Distrito
                // ---------------------------------------
                String suburb = (String) address.getOrDefault("suburb", "");
                String neighbourhood = (String) address.getOrDefault("neighbourhood", "");

                String bairro = !suburb.isBlank() ? suburb : neighbourhood;

                // ---------------------------------------
                // Construção final
                // Rua — Bairro, Cidade
                // ---------------------------------------
                String display = name;
                if (!bairro.isBlank()) display += " — " + bairro;
                if (!localCity.isBlank()) display += ", " + localCity;

                refined.add(Map.of(
                        "lat", String.valueOf(item.get("lat")),
                        "lon", String.valueOf(item.get("lon")),
                        "display_name", display
                ));
            }

            return ResponseEntity.ok(refined);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar sugestões");
        }
    }


    // ============================================================
    // 🔍 BUSCA DIRETA (filtrada e limpa)
    // ============================================================
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        try {
            String url = "https://nominatim.openstreetmap.org/search"
                    + "?format=json"
                    + "&addressdetails=1"
                    + "&limit=1"
                    + "&countrycodes=br"
                    + "&q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            List<Map<String, Object>> raw = mapper.readValue(response.body(), List.class);

            List<Map<String, String>> refined = new ArrayList<>();

            for (var item : raw) {

                Map<String, Object> address = (Map<String, Object>) item.get("address");
                if (address == null) continue;

                // Filtrar apenas SP
                String state = (String) address.getOrDefault("state", "");
                if (!state.equalsIgnoreCase("São Paulo")) continue;

                refined.add(Map.of(
                        "lat", String.valueOf(item.get("lat")),
                        "lon", String.valueOf(item.get("lon")),
                        "display_name", (String) item.get("display_name")
                ));
            }

            return ResponseEntity.ok(refined);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar endereço");
        }
    }
}
