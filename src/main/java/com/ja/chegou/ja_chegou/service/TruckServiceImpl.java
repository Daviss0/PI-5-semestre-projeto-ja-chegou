package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Driver;
import com.ja.chegou.ja_chegou.entity.Truck;
import com.ja.chegou.ja_chegou.repository.DriverRepository;
import com.ja.chegou.ja_chegou.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TruckServiceImpl implements TruckService {

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public List<Truck> findAll() {
        return truckRepository.findAll();
    }

    @Override
    public Truck findById(Long id) {
        return truckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Caminhão não encontrado"));
    }

    @Override
    public Truck save(Truck truck) {
        if (truckRepository.existsByPlate(truck.getPlate())) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }
        return truckRepository.save(truck);
    }

    @Override
    public Truck save(Truck truck, Driver driver) {
        if (truckRepository.existsByPlate(truck.getPlate())) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }
        if (driver.getTruck() != null) {
            throw new IllegalArgumentException("Motorista já está atribuído a outro caminhão");
        }
        return truckRepository.save(truck);
    }

    @Override
    public Truck update(Truck truck) {
        Truck existingTruck = truckRepository.findById(truck.getId())
                .orElseThrow(() -> new IllegalArgumentException("Caminhão não encontrado"));

        if (!existingTruck.getPlate().equals(truck.getPlate()) &&
                truckRepository.existsByPlate(truck.getPlate())) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }

        truck.setDriver(existingTruck.getDriver());
        return truckRepository.save(truck);
    }

    @Override
    public void delete(Long id) {
        Truck truck = findById(id);

        if (truck.getDriver() != null) {
            throw new IllegalArgumentException("Não é possível excluir: caminhão possui motorista atribuído");
        }

        // A partir de agora caminhão não pertence a rota → remoção segura
        truckRepository.delete(truck);
    }

    @Override
    public void assignDriver(Long truckId, Long driverId) {
        Truck truck = findById(truckId);

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado: " + driverId));

        if (driver.getTruck() != null) {
            throw new IllegalArgumentException("Motorista já está atribuído a outro caminhão");
        }

        truck.setDriver(driver);
        truckRepository.save(truck);
    }

    @Override
    public void unassignDriver(Long truckId) {
        Truck truck = findById(truckId);
        truck.setDriver(null);
        truckRepository.save(truck);
    }

    @Override
    public Map<String, String> getTruckByDriver() {
        List<Truck> trucks = truckRepository.findAll();
        Map<String, String> result = new HashMap<>();

        for (Truck truck : trucks) {
            String driverName = truck.getDriver() != null ? truck.getDriver().getName() : "Sem motorista";
            result.put(truck.getPlate(), driverName);
        }
        return result;
    }

    @Override
    public Map<String, Long> getTruckStatusSummary() {
        long actives = truckRepository.countByStatusTrue();
        long inactives = truckRepository.countByStatusFalse();
        Map<String, Long> result = new HashMap<>();
        result.put("Ativos", actives);
        result.put("Inativos", inactives);
        return result;
    }

    @Override
    public Map<String, Double> getTotalCapacityPerRoute() {
        // Caminhões não são mais vinculados a rotas. Adotamos relatório de capacidade total.
        List<Truck> trucks = truckRepository.findAll();
        Map<String, Double> result = new HashMap<>();

        result.put("Capacidade Total de Caminhões",
                trucks.stream().mapToDouble(Truck::getCapacity).sum());

        return result;
    }
}
