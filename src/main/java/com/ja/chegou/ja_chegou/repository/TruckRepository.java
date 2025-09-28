package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
    boolean existsByPlate(String plate);

    long countByStatusTrue();

    long countByStatusFalse();
}
