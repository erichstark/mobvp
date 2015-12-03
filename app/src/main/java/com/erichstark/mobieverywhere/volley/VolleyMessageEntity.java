package com.erichstark.mobieverywhere.volley;

import com.erichstark.mobieverywhere.overpass.ItemPOI;

/**
 * Created by Erich on 01/12/15.
 */
public class VolleyMessageEntity {
    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_ERROR = -1;
    private Long id;
    private String text;
    private String sent;
    private Long poi_id;
    private String poi_type;
    private String poi_name;
    private String username;
    private int status = STATUS_SENT;

    public VolleyMessageEntity() {
    }

    public VolleyMessageEntity(String text, String sent, ItemPOI itempoi, String username) {
        id = null;
        this.text = text;
        this.sent = sent;
        this.poi_id = itempoi.getId();
        this.poi_type = "node";
        this.poi_name = itempoi.getName();
        this.username = username;
        status = STATUS_SENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getPoi_id() {
        return poi_id;
    }

    public void setPoi_id(Long poi_id) {
        this.poi_id = poi_id;
    }

    public String getPoi_type() {
        return poi_type;
    }

    public void setPoi_type(String poi_type) {
        this.poi_type = poi_type;
    }

    public String getPoi_name() {
        return poi_name;
    }

    public void setPoi_name(String poi_name) {
        this.poi_name = poi_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}