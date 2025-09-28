package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Driver;
import com.ja.chegou.ja_chegou.enumerated.Role;
import com.ja.chegou.ja_chegou.enumerated.Status;
import com.ja.chegou.ja_chegou.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverServiceImpl implements DriverService{

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public List<Driver> findAll() {
        return driverRepository.findAll();
    }
    @Override
    public Driver findById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado" + id));
    }

    @Override
    public Driver save(Driver driver) {
        if (driver.getRole() == null) {
            driver.setRole(Role.DRIVER);
        }
        return driverRepository.save(driver);
    }

    @Override
    public void delete(Long id) {
        driverRepository.deleteById(id);
    }

    @Override
    public void alterarStatus(Long id) {
        Driver driver = findById(id);
        driver.setStatus(driver.getStatus().equals(Status.ATIVO) ? Status.INATIVO : Status.ATIVO);
        driverRepository.save(driver);
    }

    @Override
    public List<Driver> findAllAvailable() {
        return driverRepository.findByTruckIsNull();
    }
}
