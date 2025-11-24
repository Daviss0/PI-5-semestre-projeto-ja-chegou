package com.ja.chegou.ja_chegou.service;


import com.ja.chegou.ja_chegou.entity.CollectionJob;
import com.ja.chegou.ja_chegou.enumerated.CollectionStatus;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CollectionJobService {


    List<CollectionJob> list(CollectionStatus status, LocalDateTime from, LocalDateTime to);

    Optional<CollectionJob> findById(Long id);

    @Transactional
    CollectionJob schedule(Long routeId, Long truckId, Long driverId,
                           LocalDateTime when, String notes);

    @Transactional
    void start(Long id);


    @Transactional
    void finish(Long id, @PositiveOrZero java.math.BigDecimal weightKg, String notes);

    @Transactional
    void cancel(Long id, String reason);
}
