package com.ja.chegou.ja_chegou.gtfs.dto;

import java.util.List;

public class GTFSRouteDTO {

    private String routeId;
    private String shortName;
    private String longName;
    private double distanceToUser;
    private List<double[]> shape;

    public GTFSRouteDTO(String routeId, String shortName, String longName,
                        double distanceToUser, List<double[]> shape) {
        this.routeId = routeId;
        this.shortName = shortName;
        this.longName = longName;
        this.distanceToUser = distanceToUser;
        this.shape = shape;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    public List<double[]> getShape() {
        return shape;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public void setDistanceToUser(double distanceToUser) {
        this.distanceToUser = distanceToUser;
    }

    public void setShape(List<double[]> shape) {
        this.shape = shape;
    }
}
