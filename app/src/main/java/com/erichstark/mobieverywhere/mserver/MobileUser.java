package com.erichstark.mobieverywhere.mserver;

import java.io.Serializable;

/**
 * Created by Erich on 01/12/15.
 */
public class MobileUser implements Serializable {
    private int user_id;
    private String first_name;
    private String last_name;
    private String session_token;

    public MobileUser() {}

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getSession_token() {
        return session_token;
    }

    public void setSession_token(String session_token) {
        this.session_token = session_token;
    }

    @Override
    public String toString() {
        return "MobileUser{" +
                "user_id=" + user_id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", session_token='" + session_token + '\'' +
                '}';
    }
}
