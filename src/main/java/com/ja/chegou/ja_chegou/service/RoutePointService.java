package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.RoutePoint;
import com.ja.chegou.ja_chegou.repository.RoutePointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutePointService {

    private final RoutePointRepository repo;

    public RoutePointService(RoutePointRepository repo) {
        this.repo = repo;
    }

    public List<RoutePoint> getPointsByRoute(Long routeId) {
        return repo.findByRouteIdOrderBySequenceAsc(routeId);
    }

    public void saveAll(List<RoutePoint> points) {
        repo.saveAll(points);
    }
}