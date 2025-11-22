package com.ja.chegou.ja_chegou.DTO;

import com.ja.chegou.ja_chegou.entity.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteDTO {

    private Long id;
    private String codigoLinhaOlhoVivo;
    private Double distanceToUser;
    private String originName;

    // 🔥 Lista de coordenadas reais vinda da tabela route_point
    private List<CoordenadaDTO> coordinates = new ArrayList<>();

    // DTO simples para cada coordenada
    public record CoordenadaDTO(double lat, double lng) {}

    public RouteDTO() {}

    public RouteDTO(Route r) {
        this.id = r.getId();
        this.codigoLinhaOlhoVivo = r.getCodigoLinhaOlhoVivo();
        this.distanceToUser = r.getDistanceToUser();
        this.originName = (r.getOrigin() != null ? r.getOrigin().getName() : null);

        // ATENÇÃO:
        // Agora você NÃO usa mais r.getCoordinates() porque isso vem da tabela antiga.
        // As coordenadas reais agora são colocadas depois via setCoordinates().
        this.coordinates = new ArrayList<>();
    }

    // GETTERS & SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoLinhaOlhoVivo() { return codigoLinhaOlhoVivo; }
    public void setCodigoLinhaOlhoVivo(String codigoLinhaOlhoVivo) { this.codigoLinhaOlhoVivo = codigoLinhaOlhoVivo; }

    public Double getDistanceToUser() { return distanceToUser; }
    public void setDistanceToUser(Double distanceToUser) { this.distanceToUser = distanceToUser; }

    public String getOriginName() { return originName; }
    public void setOriginName(String originName) { this.originName = originName; }

    public List<CoordenadaDTO> getCoordinates() { return coordinates; }

    // 🔥 MÉTODO USADO PELO CONTROLLER PARA COLOCAR AS COORDENADAS REAIS
    public void setCoordinatesFromArray(List<double[]> coords) {
        this.coordinates = coords.stream()
                .map(c -> new CoordenadaDTO(c[0], c[1]))
                .toList();
    }

    public void setCoordinates(List<CoordenadaDTO> coordinates) {
        this.coordinates = coordinates;
    }
}
