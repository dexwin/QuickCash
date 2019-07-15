package com.acesdatasystems.quickcash.model;

/*
GENERAL IMPORTS
 */
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sheriff on 10/27/2018.
 */

public class SaleSender {
    /*
    Variables declarations
     */
    String personnelName;
    String personnelId;
    String AccountId;
    Date timeSent;

    /*
    Default Constructor
     */
    public SaleSender() {
    }

    /*
    Constructor to create and initialize the SaleSender Object
     */
    public SaleSender(String personnelName, String personnelId, String accountId) {
        this.personnelName = personnelName;
        this.personnelId = personnelId;
        AccountId = accountId;
        this.timeSent = new Date(System.currentTimeMillis());
    }

    /*
    Getters and Setters
     */
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
