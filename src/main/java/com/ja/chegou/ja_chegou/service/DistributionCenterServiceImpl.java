package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.DistributionCenter;
import com.ja.chegou.ja_chegou.repository.DistributionCenterRepository;
import com.ja.chegou.ja_chegou.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributionCenterServiceImpl implements DistributionCenterService{

     @Autowired
    RouteRepository routeRepository;

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

        if (center.getLatitude() == null || center.getLongitude() == null) {
            throw new IllegalArgumentException(
                    "O centro de distribuição deve possuir latitude e longitude."
            );
        }
        return distributionCenterRepository.save(center);
    }

    public void delete(Long id) {
        if (!routeRepository.findByOriginId(id).isEmpty()) {
            throw new IllegalStateException("Não é possível excluir: existem rotas vinculadas a esta central.");
        }
        distributionCenterRepository.deleteById(id);
    }

}
