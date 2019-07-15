package com.acesdatasystems.quickcash.datasource;

/*
GENERAL IMPORTS
 */
import android.support.annotation.NonNull;

import java.util.ArrayList;

/*
Constructor to initialize TableItem
 */

public class TableItem implements Comparable<TableItem>{

    private String productName = "";
    private double price = 0.0;
    private int quantity = 1;
    private boolean isTotSales = false;
    private boolean totServing = false;
    private String category = "";
    private String productId = "";
    private boolean isProductAdded = false;
    private int availableQuantity = 0;
    boolean isTapped = false;
    private ArrayList<TotProduct> totProductsList;
    private boolean isCompleted =false;
    public TableItem(){}

    /*
    Overloaded Constructor
     */
    public TableItem(String productName, double price,Boolean isTotSales) {
        this.productName = productName;
        this.price = price;
        this.isTotSales =isTotSales;
    }
    /*
    Setters and Getters
     */
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isProductAdded() {
        return isProductAdded;
    }

    public void setProductAdded(boolean productAdded) {
        isProductAdded = productAdded;
    }

    public boolean isTapped() {
        return isTapped;
    }

    public void setTapped(boolean tapped) {
        isTapped = tapped;
    }

    public boolean isTotSales() {
        return isTotSales;
    }

    public boolean isTotServing() {
        return totServing;
    }

    public void setTotServing(boolean totServing) {
        this.totServing = totServing;
    }

    public void setTotSales(boolean totSales) {
        isTotSales = totSales;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public ArrayList<TotProduct> getTotProductsList() {
        if(totProductsList == null){
            totProductsList = new ArrayList<>();
        }
        return totProductsList;
    }

    public void setTotProductsList(ArrayList<TotProduct> totProductsList) {
        this.totProductsList = totProductsList;
    }

    @Override
    public String toString(){
        return String.format("%-20s%20s",this.getProductName(),this.getPrice());
    }

    @Override
    public int compareTo(@NonNull TableItem tableItem) {
        return this.productName.compareTo(tableItem.productName);
    }
}
