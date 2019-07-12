package com.acesdatasystems.quickcash.model;

import java.util.Date;

/**
 * Created by Sheriff on 19-Sep-18.
 */

public class SubscriptionDetails {
    Date lastSubscribed = new Date();
    String plan = "STANDARD";
    Date subscriptionExpire = new Date();

    public SubscriptionDetails() {
    }

    public SubscriptionDetails(Date lastSubscribed, String plan, Date subscriptionExpire) {
        this.lastSubscribed = lastSubscribed;
        this.plan = plan;
        this.subscriptionExpire = subscriptionExpire;
    }

    public Date getLastSubscribed() {
        return lastSubscribed;
    }

    /**
     * @param lastSubscribed
     * the String value should be formatted like mm/dd/yyyy
     */
    public void setLastSubscribed(String lastSubscribed) {
        String[] dateValues = lastSubscribed.split("/");
       this.lastSubscribed =new Date(Integer.parseInt(dateValues[2]),Integer.parseInt(dateValues[0]),Integer.parseInt(dateValues[1]));
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Date getSubscriptionExpire() {
        return subscriptionExpire;
    }



    /**
     * @param subscriptionExpire
     * the String value should be formatted like mm/dd/yyyy
     */
    public void setSubscriptionExpire(String subscriptionExpire) {
        String[] dateValues = subscriptionExpire.split("/");
        this.subscriptionExpire =(new Date(Integer.parseInt(dateValues[2]),Integer.parseInt(dateValues[0]),Integer.parseInt(dateValues[1])));
    }

}
