package com.ja.chegou.ja_chegou.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "TB_ROUTES")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "distribution_center_id", nullable = false)
    @NotNull(message = "A rota deve ter uma central de origem")
    private DistributionCenter origin;

    @NotBlank(message = "O endereço de destino é obrigatório")
    @Size(max = 200, message = "O endereço de destino deve ter no máximo 200 caracteres")
    private String destinationAddress;

    @NotNull(message = "A latitude do destino é obrigatória")
    private Double destinationLatitude;

    @NotNull(message = "A longitude do destino é obrigatória")
    private Double destinationLongitude;

    @Positive(message = "A distância deve ser maior que zero")
    private Double distanceKm;

    @Positive(message = "A duração deve ser maior que zero")
    private Double durationMin;

    @Positive(message = "A velocidade média deve ser maior que zero")
    private Integer averageSpeed;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DistributionCenter getOrigin() { return origin; }
    public void setOrigin(DistributionCenter origin) { this.origin = origin; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public Double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; }

    public Double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getDurationMin() { return durationMin; }
    public void setDurationMin(Double durationMin) { this.durationMin = durationMin; }

    public Integer getAverageSpeed() {return averageSpeed;}

    public void setAverageSpeed(Integer averageSpeed) {this.averageSpeed = averageSpeed;}
}
