package com.ja.chegou.ja_chegou.DTO;
import com.ja.chegou.ja_chegou.entity.Route;

public class RouteDTO {
    private Long id;
    private String destinationAddress;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private Double originLatitude;
    private Double originLongitude;
    private Double distanceToUser;

    private boolean passaNaRua;
    public boolean isPassaNaRua() { return passaNaRua; }



    public RouteDTO() {}

    public RouteDTO(Route r) {
        this.id = r.getId();
        this.destinationAddress = r.getDestinationAddress();
        this.destinationLatitude = r.getDestinationLatitude();
        this.destinationLongitude = r.getDestinationLongitude();
        this.originLatitude = r.getOriginLatitude();
        this.originLongitude = r.getOriginLongitude();
        this.distanceToUser = r.getDistanceToUser();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public Double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; }

    public Double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; }

    public Double getOriginLatitude() { return originLatitude; }
    public void setOriginLatitude(Double originLatitude) { this.originLatitude = originLatitude; }

    public Double getOriginLongitude() { return originLongitude; }
    public void setOriginLongitude(Double originLongitude) { this.originLongitude = originLongitude; }

    public Double getDistanceToUser() { return distanceToUser; }
    public void setDistanceToUser(Double distanceToUser) { this.distanceToUser = distanceToUser; }

    public void setPassaNaRua(boolean passaNaRua) { this.passaNaRua = passaNaRua; }

}
