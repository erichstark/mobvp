package com.erichstark.mobieverywhere.overpass;

import java.util.List;
import java.util.Map;

/**
 * Created by Erich on 02/12/15.
 */
public class OPResponse {
    private String version;
    private String generator;
    private Map<String, String> osm3s;
    private List<OPElement> elements;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public Map<String, String> getOsm3s() {
        return osm3s;
    }

    public void setOsm3s(Map<String, String> osm3s) {
        this.osm3s = osm3s;
    }

    public List<OPElement> getElements() {
        return elements;
    }

    public void setElements(List<OPElement> elements) {
        this.elements = elements;
    }
}

