package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.Driver;

import java.util.List;

public interface DriverService {
    List<Driver> findAll();

    Driver findById(Long id);

    Driver save(Driver driver);

    void delete(Long id);

    void alterarStatus(Long id);

    List<Driver> findAllAvailable();
}
