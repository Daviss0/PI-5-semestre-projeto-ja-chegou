package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.entity.Truck;
import com.ja.chegou.ja_chegou.repository.RouteRepository;
import com.ja.chegou.ja_chegou.repository.TruckRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteServiceImpl implements RouteService{

    private final RouteRepository routeRepository;
    private final TruckRepository truckRepository;

    public RouteServiceImpl(RouteRepository routeRepository, TruckRepository truckRepository) {
        this.routeRepository = routeRepository;
        this.truckRepository = truckRepository;
    }

    @Override
    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    @Override
    public Route save(Route route) {
     if(route.getDestinationLatitude() == null ||
        route.getDestinationLongitude() == null ||
        route.getDistanceKm() == null || route.getDistanceKm() <= 0 ||
        route.getDurationMin() == null || route.getDurationMin() <= 0) {
         throw new IllegalArgumentException("Dados de rota invalidos");
     }
        return routeRepository.save(route);
    }

    @Override
    public void delete(Long id) {
        routeRepository.deleteById(id);
    }

    @Override
    public List<Route> getRoutesByTruck(Long truckId) {
        return routeRepository.findByTruckId(truckId);
    }



}
