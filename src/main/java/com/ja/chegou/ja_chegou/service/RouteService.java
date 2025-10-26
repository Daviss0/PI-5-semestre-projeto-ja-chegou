package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Route;

import java.util.List;

public interface RouteService {

    List<Route> findAll();

    Route save(Route route);

    void delete(Long id);

    List<Route> getRoutesByTruck(Long truckId);

    Route findById(Long id);
}
