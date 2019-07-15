package com.acesdatasystems.quickcash.datasource;

/**
 * Created by Sheriff on 25-Sep-18.
 * TotProduct Model
 */

public class TotProduct extends TableItem{
    String productName ="";
    double price = 0;
    int    totsLeft = 0;
    String productId ="";
    int quantity = 1;
    int totsPerBottle =0;
    boolean isTot = false;

    boolean isTapped = false;

    /*
    constructor to create object and initialize TotProduct
     */
    public TotProduct(String name, double price) {
        this.productName = name;
        this.price = price;
    }
    public TotProduct(String name, double price, int totsLeft, String productId, int totsPerBottle) {
        this.productName = name;
        this.price = price;
        this.totsLeft = totsLeft;
        this.productId = productId;
        this.totsPerBottle = totsPerBottle;

    }

    /*
    Setters and Getters
     */

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }
    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isTapped() {
        return isTapped;
    }

    public void setTapped(boolean tapped) {
        isTapped = tapped;
    }

    public int getTotsLeft() {
        return totsLeft;
    }

    public void setTotsLeft(int totsLeft) {
        this.totsLeft = totsLeft;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getTotsPerBottle() {
        return totsPerBottle;
    }

    public void setTotsPerBottle(int totsPerBottle) {
        this.totsPerBottle = totsPerBottle;
    }

    public boolean isTot() {
        return isTot;
    }

    public void setTot(boolean tot) {
        isTot = tot;
    }

    @Override
    public String toString(){
        return String.format("%-20s%20s",this.getProductName(),this.getPrice());
    }

}
