package com.acesdatasystems.quickcash.model;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sheriff on 10/27/2018.
 */

public class Sale {
    String saleId;
    Long timeSold;
    double totCost;
    double totalDiscount;
    String personnelName;
    String personnelId;

    public Sale() {
    }

    public Sale(String saleId, Date dateSold, double totCost, double totalDiscount, String personnelName, String personnelId) {
        this.saleId = saleId;
        this.timeSold = dateSold.getTime();
        this.totCost = totCost;
        this.totalDiscount = totalDiscount;
        this.personnelName = personnelName;
        this.personnelId = personnelId;
    }



    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public long getTimeSold() {

     return timeSold;
    }
    public String getTimeSoldASString() {

        return new SimpleDateFormat(" dd-MM-yyyy", Locale.getDefault()).format(new Date(timeSold));
    }

    public void setTimeSold(long dateSold) {

        this.timeSold = dateSold;
    }

    public double getTotCost() {
        return totCost;
    }

    public void setTotCost(double totCost) {
        this.totCost = totCost;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
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

    public static long convertDateToLong(String date){
        System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy                         yyyyyyyyy " + date);
        if (date.matches("\\s*\\d+-\\d+-\\d+")) {
            String[] dateComponets = date.split("-");
            Date date1 = new Date(Integer.parseInt(dateComponets[dateComponets.length - 1].trim()), Integer.parseInt(dateComponets[dateComponets.length - 2].trim()), Integer.parseInt(dateComponets[dateComponets.length - 3].trim()));
            return date1.getTime();
        }else if(date.matches("\\d+")){
            return Long.parseLong(date.trim());
        }else{
            return 0l;
        }
    }

}
