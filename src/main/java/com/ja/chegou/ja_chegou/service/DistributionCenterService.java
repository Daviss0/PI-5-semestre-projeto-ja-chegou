package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.DistributionCenter;

import java.util.List;

public interface DistributionCenterService {
    List<DistributionCenter> findAll();

    DistributionCenter save(DistributionCenter center);

    void delete(Long id);
}
