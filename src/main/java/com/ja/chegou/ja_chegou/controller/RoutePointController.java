package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.RoutePoint;
import com.ja.chegou.ja_chegou.service.RoutePointService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RoutePointController {

    private final RoutePointService routePointService;

    public RoutePointController(RoutePointService routePointService) {
        this.routePointService = routePointService;
    }

    @GetMapping("/{routeId}/points")
    public List<RoutePoint> getRoutePoints(@PathVariable Long routeId) {
        return routePointService.getPointsByRoute(routeId);
    }
}