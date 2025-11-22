package com.ja.chegou.ja_chegou.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "TB_ROUTES")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Central de origem (opcional)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "distribution_center_id")
    private DistributionCenter origin;

    // Código da linha SPTrans
    private String codigoLinhaOlhoVivo;

    // Distância total do shape (km)
    private Double distanceKm;

    // Distância do cliente até o ponto mais próximo da rota (não persistido)
    @Transient
    private Double distanceToUser;


    // Classe embutida que representa um ponto do shape
    @Embeddable
    public static class Coordenada {

        private double lat;
        private double lng;

        public Coordenada() {}

        public Coordenada(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DistributionCenter getOrigin() { return origin; }
    public void setOrigin(DistributionCenter origin) { this.origin = origin; }

    public String getCodigoLinhaOlhoVivo() { return codigoLinhaOlhoVivo; }
    public void setCodigoLinhaOlhoVivo(String codigoLinhaOlhoVivo) { this.codigoLinhaOlhoVivo = codigoLinhaOlhoVivo; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getDistanceToUser() { return distanceToUser; }
    public void setDistanceToUser(Double distanceToUser) { this.distanceToUser = distanceToUser; }

}
