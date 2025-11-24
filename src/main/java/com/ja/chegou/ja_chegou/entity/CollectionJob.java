package com.ja.chegou.ja_chegou.entity;

import com.ja.chegou.ja_chegou.enumerated.CollectionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "TB_COLLECTIONS",
        indexes = {
                @Index(name = "idx_collections_status", columnList = "status"),
                @Index(name = "idx_collections_scheduled_at", columnList = "scheduledAt")
        }
)
public class CollectionJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A rota é obrigatória")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Route route;

    @NotNull(message = "O caminhão é obrigatório")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Truck truck;

    @NotNull(message = "O motorista é obrigatório")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Driver driver;

    @NotNull(message = "A data/hora agendada é obrigatória")
    private LocalDateTime scheduledAt;

    @PastOrPresent(message = "O início não pode estar no futuro")
    private LocalDateTime startedAt;

    @PastOrPresent(message = "O término não pode estar no futuro")
    private LocalDateTime finishedAt;

    @PastOrPresent(message = "O cancelamento não pode estar no futuro")
    private LocalDateTime canceledAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private CollectionStatus status = CollectionStatus.AGENDADA;

    @Size(max = 1000, message = "Observações podem ter no máximo 1000 caracteres")
    @Column(length = 1000)
    private String notes;

    @PositiveOrZero(message = "O peso coletado deve ser zero ou positivo.")
    @Digits(integer = 10, fraction = 2, message = "Peso inválido (máx. 2 casas decimais).")
    @Column(name = "collected_weight_kg", precision = 12, scale = 2)
    private BigDecimal collectedWeightKg;


    // Se iniciou, status deve ser EM_ANDAMENTO ou CONCLUIDA, e startedAt >= scheduledAt (quando existir)
    @AssertTrue(message = "Início inválido: quando 'startedAt' está preenchido, o status deve ser EM_ANDAMENTO ou CONCLUIDA.")
    private boolean isStartConsistent() {
        if (startedAt == null) return true;
        return status == CollectionStatus.EM_ANDAMENTO
                || status == CollectionStatus.CONCLUIDA
                || status == CollectionStatus.CANCELADA;
    }


    // Se terminou, status deve ser CONCLUIDA, deve ter startedAt e finishedAt >= startedAt
    @AssertTrue(message = "Coleta concluída exige status CONCLUIDA, início informado e 'finishedAt' não anterior a 'startedAt'.")
    private boolean isFinishConsistent() {
        if (finishedAt == null) return true;
        return status == CollectionStatus.CONCLUIDA
                && startedAt != null
                && !finishedAt.isBefore(startedAt);
    }

    // Se cancelou, status deve ser CANCELADA e não pode ter finishedAt
    @AssertTrue(message = "Cancelada exige status CANCELADA e não pode ter 'finishedAt' preenchido.")
    private boolean isCancelConsistent() {
        if (canceledAt == null) return true;
        return status == CollectionStatus.CANCELADA && finishedAt == null;
    }

    // Status AGENDADA não pode ter início/término/cancelamento
    @AssertTrue(message = "Status AGENDADA não pode ter início, término ou cancelamento.")
    private boolean isScheduledClean() {
        if (status != CollectionStatus.AGENDADA) return true;
        return startedAt == null && finishedAt == null && canceledAt == null;
    }

    //getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Truck getTruck() { return truck; }
    public void setTruck(Truck truck) { this.truck = truck; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    public LocalDateTime getCanceledAt() { return canceledAt; }
    public void setCanceledAt(LocalDateTime canceledAt) { this.canceledAt = canceledAt; }

    public CollectionStatus getStatus() { return status; }
    public void setStatus(CollectionStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public BigDecimal getCollectedWeightKg() { return collectedWeightKg; }
    public void setCollectedWeightKg(BigDecimal collectedWeightKg) { this.collectedWeightKg = collectedWeightKg; }
}
