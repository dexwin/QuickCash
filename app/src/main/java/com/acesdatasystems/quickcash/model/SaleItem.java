package com.acesdatasystems.quickcash.model;

/**
 * Created by Sheriff on 10/27/2018.
 */

public class SaleItem {
    String saleId;
    String productId;
    String itemName;
    boolean isTotProduct;
    int quantitySold;
    int availableQuantity;
    double totalDiscount;
    long timeSold;
    double cost;

    public SaleItem() {
    }

    public SaleItem(String saleId, String productId, String itemName, boolean isTotProduct, int quantitySold, int availableQuantity, double totalDiscount, double cost) {
        this.saleId = saleId;
        this.productId = productId;
        this.itemName = itemName;
        this.isTotProduct = isTotProduct;
        this.quantitySold = quantitySold;
        this.availableQuantity = availableQuantity;
        this.totalDiscount = totalDiscount;
        this.timeSold =System.currentTimeMillis();
        this.cost = cost;

    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isTotProduct() {
        return isTotProduct;
    }

    public void setTotProduct(boolean totProduct) {
        isTotProduct = totProduct;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public long getTimeSold() {
        return timeSold;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setTimeSold(long timeSold) {
        this.timeSold = timeSold;
    }
}
