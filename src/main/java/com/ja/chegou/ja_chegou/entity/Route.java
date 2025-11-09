package com.ja.chegou.ja_chegou.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "TB_ROUTES")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ====== Origem ======
    @ManyToOne
    @JoinColumn(name = "distribution_center_id", nullable = false)
    @NotNull(message = "A rota deve ter uma central de origem")
    private DistributionCenter origin;

    // 🔹 Coordenadas da origem (pode ser populada a partir do DistributionCenter)
    private Double originLatitude;
    private Double originLongitude;

    // ====== Destino ======
    @NotBlank(message = "O endereço de destino é obrigatório")
    @Size(max = 200, message = "O endereço de destino deve ter no máximo 200 caracteres")
    private String destinationAddress;

    @NotNull(message = "A latitude do destino é obrigatória")
    private Double destinationLatitude;

    @NotNull(message = "A longitude do destino é obrigatória")
    private Double destinationLongitude;

    // ====== Dados adicionais ======
    @Positive(message = "A distância deve ser maior que zero")
    private Double distanceKm;

    @Positive(message = "A duração deve ser maior que zero")
    private Double durationMin;

    @Positive(message = "A velocidade média deve ser maior que zero")
    private Integer averageSpeed;

    @Positive(message = "A capacidade mínima da rota deve ser maior que zero")
    private Double requiredCapacity;

    @ManyToOne
    @JoinColumn(name = "truck_id")
    private Truck truck;

    // ====== Campos transitórios (não vão para o banco) ======
    @Transient
    private Double distanceToUser;  // distância calculada até o cliente

    @Transient
    private List<Coordenada> coordinates; // lista de pontos da rota (não persistida)

    // ====== Classe auxiliar para coordenadas ======
    public static class Coordenada {
        private double lat;
        private double lng;

        public Coordenada(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    // ====== Getters e Setters ======

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DistributionCenter getOrigin() { return origin; }
    public void setOrigin(DistributionCenter origin) { this.origin = origin; }

    public Double getOriginLatitude() { return originLatitude; }
    public void setOriginLatitude(Double originLatitude) { this.originLatitude = originLatitude; }

    public Double getOriginLongitude() { return originLongitude; }
    public void setOriginLongitude(Double originLongitude) { this.originLongitude = originLongitude; }

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

    public Integer getAverageSpeed() { return averageSpeed; }
    public void setAverageSpeed(Integer averageSpeed) { this.averageSpeed = averageSpeed; }

    public Double getRequiredCapacity() { return requiredCapacity; }
    public void setRequiredCapacity(Double requiredCapacity) { this.requiredCapacity = requiredCapacity; }

    public Truck getTruck() { return truck; }
    public void setTruck(Truck truck) { this.truck = truck; }

    public Double getDistanceToUser() { return distanceToUser; }
    public void setDistanceToUser(Double distanceToUser) { this.distanceToUser = distanceToUser; }

    public List<Coordenada> getCoordinates() { return coordinates; }
    public void setCoordinates(List<Coordenada> coordinates) { this.coordinates = coordinates; }
}
