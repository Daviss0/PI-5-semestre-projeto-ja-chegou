package com.ja.chegou.ja_chegou.repository;

import com.ja.chegou.ja_chegou.entity.CollectionJob;
import com.ja.chegou.ja_chegou.enumerated.CollectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CollectionJobRepository extends JpaRepository<CollectionJob, Long> {

    @Query("""
       select c from CollectionJob c
       where (:status is null or c.status = :status)
         and (:from   is null or c.scheduledAt >= :from)
         and (:to     is null or c.scheduledAt <  :to)
       order by c.scheduledAt desc
    """)
    List<CollectionJob> search(@Param("status") CollectionStatus status,
                               @Param("from")   LocalDateTime from,
                               @Param("to")     LocalDateTime to);

    @Query(value = """
        select case when count(*) > 0 then true else false end
        from TB_COLLECTIONS c
        where c.TRUCK_ID = :truckId
          and c.STATUS in ('AGENDADA','EM_ANDAMENTO')
    """, nativeQuery = true)
    boolean existsActiveByTruckId(@Param("truckId") Long truckId);

    @Query(value = """
        select case when count(*) > 0 then true else false end
        from TB_COLLECTIONS c
        where c.DRIVER_ID = :driverId
          and c.STATUS in ('AGENDADA','EM_ANDAMENTO')
    """, nativeQuery = true)
    boolean existsActiveByDriverId(@Param("driverId") Long driverId);
}

