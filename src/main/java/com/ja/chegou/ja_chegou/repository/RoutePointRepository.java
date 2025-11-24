package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.RoutePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
    List<RoutePoint> findByRouteIdOrderBySequenceAsc(Long routeId);
}