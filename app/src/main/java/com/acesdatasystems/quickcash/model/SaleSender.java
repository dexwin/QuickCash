package com.acesdatasystems.quickcash.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sheriff on 10/27/2018.
 */

public class SaleSender {
    String personnelName;
    String personnelId;
    String AccountId;
    Date timeSent;

    public SaleSender() {
    }

    public SaleSender(String personnelName, String personnelId, String accountId) {
        this.personnelName = personnelName;
        this.personnelId = personnelId;
        AccountId = accountId;
        this.timeSent = new Date(System.currentTimeMillis());
    }

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public String getAccountId() {
        return AccountId;
    }

    public void setAccountId(String accountId) {
        AccountId = accountId;
    }

    public String getTimeSent() {

        return new SimpleDateFormat(" yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(timeSent);
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }


}
