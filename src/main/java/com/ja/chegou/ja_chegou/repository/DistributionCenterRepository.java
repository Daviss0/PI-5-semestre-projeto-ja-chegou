package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.DistributionCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionCenterRepository extends JpaRepository<DistributionCenter, Long> {
}
