package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Route;

import java.util.List;

public interface RouteService {

    List<Route> findAll();

    Route save(Route route);

    void delete(Long id);

    List<Route> getRoutesByTruck(Long truckId);

    Route findById(Long id);

    double calcularDistancia(double lat1, double lon1, double lat2, double lon2);

    List<Route> findClosestRoutes(double userLat, double userLon);

    void gerarCoordenadasSeNecessario(Route r);
}
