package com.ja.chegou.ja_chegou.service;

import com.ja.chegou.ja_chegou.entity.CollectionJob;
import com.ja.chegou.ja_chegou.enumerated.CollectionStatus;
import com.ja.chegou.ja_chegou.repository.CollectionJobRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CollectionJobServiceImpl implements CollectionJobService {

    private final CollectionJobRepository repo;
    private final RouteService routeService;
    private final TruckService truckService;
    private final DriverService driverService;

    public CollectionJobServiceImpl(CollectionJobRepository repo,
                                    RouteService routeService,
                                    TruckService truckService,
                                    DriverService driverService) {
        this.repo = repo;
        this.routeService = routeService;
        this.truckService = truckService;
        this.driverService = driverService;
    }

    @Override
    public List<CollectionJob> list(CollectionStatus status, LocalDateTime from, LocalDateTime to) {
        return repo.search(status, from, to);
    }

    @Override
    public Optional<CollectionJob> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    @Transactional
    public CollectionJob schedule(Long routeId, Long truckId, Long driverId,
                                  LocalDateTime when, String notes) {
        LocalDateTime now = LocalDateTime.now(/* ZoneId.of("America/Sao_Paulo") */);
        LocalDateTime scheduled = (when != null ? when : now);

        if (scheduled.isBefore(now.minusMinutes(1))) {
            throw new IllegalArgumentException("A data/hora agendada não pode estar no passado.");
        }

        var route  = routeService.findById(routeId);
        var truck  = truckService.findById(truckId);
        var driver = driverService.findById(driverId);

        if (repo.existsActiveByTruckId(truckId)) {
             throw new IllegalStateException("Já existe coleta ativa para este caminhão.");
         }
         if (repo.existsActiveByDriverId(driverId)) {
             throw new IllegalStateException("Já existe coleta ativa para este motorista.");
         }

        var job = new CollectionJob();
        job.setRoute(route);
        job.setTruck(truck);
        job.setDriver(driver);
        job.setScheduledAt(scheduled);
        job.setStatus(CollectionStatus.AGENDADA);
        job.setNotes((notes != null && !notes.isBlank()) ? notes.trim() : null);

        return repo.save(job);
    }


    @Transactional
    public void start(Long id) {
        var job = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coleta " + id + " não encontrada"));

        if (job.getStatus() == CollectionStatus.EM_ANDAMENTO) {
            return;
        }

        if (job.getStatus() != CollectionStatus.AGENDADA) {
            throw new IllegalStateException("Só é possível iniciar coletas AGENDADAS. Atual: " + job.getStatus());
        }
        if (job.getRoute() == null || job.getTruck() == null || job.getDriver() == null || job.getScheduledAt() == null) {
            throw new IllegalStateException("Dados obrigatórios ausentes (rota/caminhão/motorista/data).");
        }
        if (job.getFinishedAt() != null) {
            throw new IllegalStateException("Coleta já finalizada.");
        }

        job.setStatus(CollectionStatus.EM_ANDAMENTO);
        job.setStartedAt(LocalDateTime.now());

        repo.save(job);
    }


    @Transactional
    @Override
    public void finish(Long id, @PositiveOrZero java.math.BigDecimal weightKg, String notes) {
        var job = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coleta " + id + " não encontrada"));
        if (job.getStatus() != CollectionStatus.EM_ANDAMENTO) {
            throw new IllegalArgumentException("Só é possível concluir coletas EM_ANDAMENTO");
        }

        job.setStatus(CollectionStatus.CONCLUIDA);
        job.setFinishedAt(LocalDateTime.now());

        if (weightKg != null) {
            job.setCollectedWeightKg(weightKg);
        }
        if (notes != null && !notes.isBlank()) {
            job.setNotes(notes);
        }
    }

    @Override
    @Transactional
    public void cancel(Long id, String reason) {
        var job = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coleta " + id + " não encontrada"));

        if (job.getStatus() == CollectionStatus.CONCLUIDA) {
            throw new IllegalArgumentException("Não é possível cancelar uma coleta já concluída");
        }

        String r = (reason == null || reason.isBlank()) ? "Cancelado pelo sistema" : reason.trim();

        job.setStatus(CollectionStatus.CANCELADA);
        job.setCanceledAt(java.time.LocalDateTime.now());

        job.setFinishedAt(null);

        String prev = (job.getNotes() == null || job.getNotes().isBlank()) ? "" : job.getNotes().trim() + " | ";
        String newNotes = prev + "Cancelada: " + r;
        job.setNotes(truncate(newNotes, 1000));
    }

    private static String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) : s;
    }

}
