package com.acesdatasystems.quickcash.model;

import com.acesdatasystems.quickcash.datasource.TableItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sheriff on 12/6/2018.
 */

public class KitchenProduct {
    String tableNumber;
    String waitressName;
    ArrayList<TableItem> tableItems = new ArrayList<>();
    boolean complted;
    String productReference;
    boolean workingOn = false;
    String tableComment = "";
    public static HashMap<String,String> orderTableKitchenIdList = new HashMap();


    public KitchenProduct() {
    }

    public KitchenProduct(String tableNumber, String waitressName, ArrayList<TableItem> tableItems, boolean complted) {
        this.tableNumber = tableNumber;
        this.waitressName = waitressName;
        this.tableItems = tableItems;
        this.complted = complted;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getWaitressName() {
        return waitressName;
    }

    public void setWaitressName(String waitressName) {
        this.waitressName = waitressName;
    }

    public ArrayList<TableItem> getTableItems() {
        return tableItems;
    }

    public void setTableItems(ArrayList<TableItem> tableItems) {
        this.tableItems = tableItems;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public boolean isComplted() {
        return complted;
    }

    public void setComplted(boolean complted) {
        this.complted = complted;
    }

    public boolean isWorkingOn() {
        return workingOn;
    }

    public void setWorkingOn(boolean workingOn) {
        this.workingOn = workingOn;
    }

    public String getProductReference() {
        return productReference;
    }

    public void setProductReference(String productReference) {
        this.productReference = productReference;
    }

    public static String getNotifcationTagLine(KitchenProduct kitchenProduct){
        String string =null;
        String notificationTagLine = "";
        for (TableItem tableItem : kitchenProduct.getTableItems()){
            if (tableItem !=null)
            notificationTagLine += " " + tableItem.getProductName();
        }
        return notificationTagLine.trim();
    }

    @Override
    public String toString() {
        return "KitchenProduct{" +
                "tableNumber='" + tableNumber + '\'' +
                ", waitressName='" + waitressName + '\'' +
                ", complted=" + complted +
                ", productReference='" + productReference + '\'' +
                ", workingOn=" + workingOn +
                ", tableComment='" + tableComment + '\'' +
                '}';
    }
}
