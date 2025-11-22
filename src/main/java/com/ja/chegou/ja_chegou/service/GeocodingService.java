package com.ja.chegou.ja_chegou.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double[] geocode(String address) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" +
                    address.replace(" ", "+") + "+São+Paulo";

            List<Map<String, Object>> response =
                    restTemplate.getForObject(url, List.class);

            if (response == null || response.isEmpty())
                return null;

            double lat = Double.parseDouble((String) response.get(0).get("lat"));
            double lon = Double.parseDouble((String) response.get(0).get("lon"));

            return new double[]{lat, lon};

        } catch (Exception e) {
            System.err.println("Erro ao geocodificar: " + e.getMessage());
            return null;
        }
    }
}