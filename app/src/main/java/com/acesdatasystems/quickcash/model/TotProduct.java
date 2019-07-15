package com.acesdatasystems.quickcash.model;

/**
 * Created by Sheriff on 25-Sep-18.
 */

public class TotProduct {

    /*
    variable declaration and initialization
     */
    String name ="";
    double price = 0;
    int totLeft = 0;
    String productId ="";
    int totsPerBottle =0;


    /*
    Default Constructor
     */

    public TotProduct() {
    }

    /*
    constructor to instantiate and initialize TotProduct Object
     */
    public TotProduct(String name, double price, int totsLeft, String productId, int totsPerBottle) {
        this.name = name;
        this.price = price;
        this.totLeft = totsLeft;
        this.productId = productId;
        this.totsPerBottle = totsPerBottle;
    }

    /*
    Setters and Getters
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotLeft() {
        return totLeft;
    }

    public void setTotLeft(int totLeft) {
        this.totLeft = totLeft;
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


}
