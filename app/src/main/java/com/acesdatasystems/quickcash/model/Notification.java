package com.acesdatasystems.quickcash.model;

/**
 * Created by Sheriff on 11/17/2018.
 */

public class Notification {
    private String message;
    private String tag;
    private String token;

    public Notification() {
    }

    public Notification(String message, String tag, String token) {
        this.message = message;
        this.tag = tag;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
