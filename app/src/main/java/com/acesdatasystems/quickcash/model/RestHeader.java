package com.acesdatasystems.quickcash.model;

import android.util.Log;

import com.acesdatasystems.quickcash.datasource.*;
import com.acesdatasystems.quickcash.datasource.TotProduct;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Sheriff on 10/27/2018.
 */

public class RestHeader {
    SaleSender senderDetails;
    Sale saleInfo;
//    SaleItem[] saleItems;
    List<SaleItem> saleItems = new ArrayList<>();

    public RestHeader() {
    }

    public RestHeader(SaleSender senderDetails, Sale saleInfo, List<SaleItem> saleItems) {
        this.senderDetails = senderDetails;
        this.saleInfo = saleInfo;
        this.saleItems = saleItems;
    }

    public SaleSender getSenderDetails() {
        return senderDetails;
    }

    public void setSenderDetails(SaleSender senderDetails) {
        this.senderDetails = senderDetails;
    }

    public Sale getSaleInfo() {
        return saleInfo;
    }

    public void setSaleInfo(Sale saleInfo) {
        this.saleInfo = saleInfo;
    }

//    public SaleItem[] getSaleItems() {
//        return saleItems;
//    }
//
//    public void setSaleItems(SaleItem[] saleItems) {
//        this.saleItems = saleItems;
//    }


    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public  RestHeader createHeader(List<TableItem> tableItems, AccountUser accountUser, double totalDiscount, double totalCost){
        Date date = new Date(System.currentTimeMillis());
        Sale saleDetails = new Sale( "-1",date  , totalCost, totalDiscount,  accountUser.getName(), accountUser.getUserId());
//        SaleItem[] items = new SaleItem[tableItems.size()];
        List<SaleItem> items = new ArrayList<>();
        int i =0;
        for (TableItem tpd: tableItems){
            SaleItem saleItem = new SaleItem( "-1",  tpd.getProductId(),  tpd.getProductName(),   tpd.isTotSales(), tpd.getQuantity(), tpd.getAvailableQuantity(),  totalDiscount, tpd.getPrice());
//            System.out.println(items[i].availableQuantity+ " " + items[i].getItemName() + " " + items[i].getTotalDiscount());
//            items[i] = saleItem;
            items.add(saleItem);
        }

        SaleSender saleSender = new SaleSender(accountUser.getName(),accountUser.getUserId(),accountUser.getRestaurant());
        RestHeader restHeader = new RestHeader(saleSender,saleDetails, items);
//        Gson gson = new Gson();
//        String json = gson.toJson(restHeader);
//        sendPost(restHeader);
        return  restHeader;
    }

    public static void sendPost(final RestHeader restHeader) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Gson gson = new Gson();
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.putOpt("saleInfo", gson.toJson(restHeader.getSaleInfo()));
                    jsonParam.putOpt("saleItems", gson.toJson(restHeader.getSaleItems()));
                    jsonParam.putOpt("senderDetails", gson.toJson(restHeader.getSenderDetails()));

                    System.out.println(jsonParam);
                    String query = jsonParam.toString().replace("\"{", "{").replace("}\"","}");
                    query  = query.replace("\\", "").replace("\"[","[").replace("]\"","]");
                    Log.i("JSON", query);
//
                    HttpPost httpPost = new HttpPost("https://us-central1-quick-cash-32ecf.cloudfunctions.net/sell");
                    StringEntity entity = new StringEntity(query);
                    httpPost.setHeader("Content-type","Application/json");
                    httpPost.setEntity(entity);
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    BasicResponseHandler responseHandler = new BasicResponseHandler();
                    httpClient.execute(httpPost, responseHandler);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }




}
