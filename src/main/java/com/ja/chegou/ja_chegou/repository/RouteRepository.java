package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.Route;
import com.ja.chegou.ja_chegou.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByOriginId(Long centerId);

}
