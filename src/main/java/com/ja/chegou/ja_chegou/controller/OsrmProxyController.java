package com.ja.chegou.ja_chegou.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/osrm")
public class OsrmProxyController {

    private final WebClient web;

    public OsrmProxyController(@Value("${osrm.base-url}") String baseUrl) {
        this.web = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @GetMapping("/route")
    public Mono<ResponseEntity<String>> route(
         @RequestParam double fromLat,
         @RequestParam double fromLng,
         @RequestParam double toLat,
         @RequestParam double toLng,
         @RequestParam(defaultValue = "full") String overview,
         @RequestParam(defaultValue = "geojson") String geometries) {

        String coordinates = String.format("%f,%f;%f,%f", fromLng, fromLat, toLng, toLat);

        String uri = String.format("/route/v1/driving/%s?overview=%s&geometries=%s",
                coordinates, overview, geometries);

        return web.get()
                .uri(uri)
                .retrieve()
                .onStatus(
                        s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .defaultIfEmpty("<<sem corpo>>")
                                .flatMap(body -> {
                                    System.err.println("OSRM ERROR " + resp.statusCode() + " :: " + body);
                                    return Mono.error(new RuntimeException("OSRM " + resp.statusCode() + ": " + body));
                                })
                )
                .toEntity(String.class)
                .doOnError(err ->
                        System.err.println("Proxy OSRM falhou: " + err.getMessage())
                );
    }
}