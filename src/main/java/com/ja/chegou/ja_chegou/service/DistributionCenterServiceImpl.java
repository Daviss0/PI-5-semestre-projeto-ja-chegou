package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.DistributionCenter;
import com.ja.chegou.ja_chegou.repository.DistributionCenterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributionCenterServiceImpl implements DistributionCenterService{


    private final DistributionCenterRepository distributionCenterRepository;

    public DistributionCenterServiceImpl(DistributionCenterRepository distributionCenterRepository) {
        this.distributionCenterRepository = distributionCenterRepository;
    }

    @Override
    public List<DistributionCenter> findAll() {
        return distributionCenterRepository.findAll();
    }

    @Override
    public DistributionCenter save(DistributionCenter center) {
        return distributionCenterRepository.save(center);
    }

    @Override
    public void delete(Long id) {
        distributionCenterRepository.deleteById(id);
    }
}
