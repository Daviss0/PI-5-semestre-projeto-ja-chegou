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

    // Código da linha SPTrans (ex: "546L-10")
    private String codigoLinhaOlhoVivo;

    // Distância total do shape (km)
    private Double distanceKm;

    // Shape completo da rota (lista de coordenadas)
    @ElementCollection
    @CollectionTable(name = "TB_ROUTE_SHAPE", joinColumns = @JoinColumn(name = "route_id"))
    private List<Coordenada> shape;

    // Distância até o usuário (não persiste)
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

    public List<Coordenada> getShape() { return shape; }
    public void setShape(List<Coordenada> shape) { this.shape = shape; }

    public Double getDistanceToUser() { return distanceToUser; }
    public void setDistanceToUser(Double distanceToUser) { this.distanceToUser = distanceToUser; }
}
