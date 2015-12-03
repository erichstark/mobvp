package com.erichstark.mobieverywhere.overpass;

/**
 * Created by Erich on 02/12/15.
 */
public class ItemPOI {
    private Long id;
    private String name;
    private double distance;

    public ItemPOI(Long id, String name, double distance) {
        this.id = id;
        this.name = name;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
