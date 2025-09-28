package com.ja.chegou.ja_chegou.service;


import com.ja.chegou.ja_chegou.entity.Driver;
import com.ja.chegou.ja_chegou.entity.Truck;

import java.util.List;
import java.util.Map;

public interface TruckService {
    List<Truck> findAll();

    Truck findById(Long id);

    Truck save(Truck truck);

    Truck save(Truck truck, Driver driver);

    Truck update(Truck truck);

    void delete(Long id);

    void assignDriver(Long truckId, Long driverId);

    void unassignDriver(Long truckId);

    Map<String, String> getTruckByDriver();

    Map<String, Long> getTruckStatusSummary();

    Map<String, Double> getTotalCapacityPerRoute();
}
