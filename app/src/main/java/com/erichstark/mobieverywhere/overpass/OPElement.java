package com.erichstark.mobieverywhere.overpass;

import java.util.Map;

/**
 * Created by Erich on 02/12/15.
 */

public class OPElement {
    private Long id;
    private Float lat;
    private Float lon;
    private Map<String, String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public String getTag(String key) {
        String value = tags.get(key);
        return value == null ? "" : value;
    }
}