package com.acesdatasystems.quickcash;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.acesdatasystems.quickcash.model.Sale;
import com.acesdatasystems.quickcash.model.SaleItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SalesActivity extends BaseActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager salesViewPager;
    private ImageView imgSalesBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        imgSalesBack = findViewById(R.id.imgSalesBack);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // Set up the ViewPager with the sections adapter.
        salesViewPager = (ViewPager) findViewById(R.id.salesContainer);
        setupViewPager(salesViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.salesActivityTabs);
        tabLayout.setupWithViewPager(salesViewPager);


        salesBackOnAction();
        isSalesActivityStarted = true;


    }
    public void salesBackOnAction(){
        imgSalesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesActivity.super.onBackPressed();
            }
        });
    }


    public void setupViewPager(ViewPager viewPager){
        SalesSectionsPagerAdapter adapter = new SalesSectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TodaySalesFragment(),"SUMMARY");
//        adapter.addFragment(new SummarySalesFragment(),"SUMMARY");
//        adapter.addFragment(new DetailSalesFragment(),"DETAIL");
        viewPager.setAdapter(adapter);
    }


    public void loadNext100Sales(int offset,long startDate, long endDate){
        String accountId = LoginActivity.currentAccountUser.getRestaurant();
        Query query = LoginActivity.accountsRef.child(accountId).child("sales").orderByChild("timeSold").startAt(startDate).endAt(endDate).limitToFirst(offset);
        final List<Sale> saleList = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Sale sale = (Sale) snapshot.getValue(Sale.class);
                        saleList.add(sale);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    }
                //todo barry you have your list of the first hundred sales
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            // todo handle error here
            }
        });

    }

    public void loadSalesBasesOnPersonnel(String personellId, long startDate, long endDate, int datalenght){
        String accountId = LoginActivity.currentAccountUser.getRestaurant();
        final Query query = LoginActivity.accountsRef.child(accountId).child("sales").orderByChild("personnelId").equalTo(personellId).orderByChild("timeSold").startAt(startDate).endAt(endDate).limitToFirst(datalenght);
        final List<Sale> saleList = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Sale sale = (Sale) snapshot.getValue(Sale.class);
                        saleList.add(sale);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                //todo barry you have your list of the first hundred sales
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo handle error here
            }
        });

    }

    public void loadSalesBasedOnProduct(String productName,long startDate,long endDate ,int lenghtOfData){
        String accountId = LoginActivity.currentAccountUser.getRestaurant();
        Query query = LoginActivity.accountsRef.child(accountId).child("saleItems").orderByChild("itemName").equalTo(productName).orderByChild("timeSold").startAt(startDate).endAt(endDate).limitToFirst(lenghtOfData);
        final List<SaleItem> saleList = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaleItem sale = (SaleItem) snapshot.getValue(SaleItem.class);
                    saleList.add(sale);
                }

                //todo barry you have your list of the first hundred sales
//                saleList
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo handle error here
            }
        });
    }

    //todo barry call this method when loading sale. the resulst should be handled at loadSalesBasesOnPersonnel
    public void loadSalesDetail(String queryType, Date date, String searchWord, String searchField ,int dataLength){
        long startDate;
        long endDate ;
        final long millisecondsPerDay =86400000;
        if (searchField.toLowerCase().contains("wait")){
            //looading sales based on water
            switch (queryType){
                case "DAY":
                   startDate = getLongDateAt12(date) + millisecondsPerDay;
                   endDate = getLongDateAt12(date) + millisecondsPerDay;
                    loadSalesBasesOnPersonnel(searchWord,startDate,endDate,dataLength);
                    break;
                case "WEEK":
                     startDate = getLongDateAt12(date) - (date.getDay() * millisecondsPerDay);
                     endDate = getLongDateAt12(date) + ((7-date.getDay())* millisecondsPerDay);
                     loadSalesBasesOnPersonnel(searchField,startDate,endDate,dataLength);
                     break;
                default:
                     startDate = new Date(date.getYear(),date.getMonth(),1).getTime();
                     endDate = (new Date(date.getYear(),(date.getMonth()+1),1).getTime()) - millisecondsPerDay;
                    loadSalesBasesOnPersonnel(searchField,startDate,endDate,dataLength);
                    break;
            }

        }else if (searchField.toLowerCase().contains("product")){
            switch (queryType){
                case "DAY":
                    startDate = getLongDateAt12(date);
                    endDate = getLongDateAt12(date) + millisecondsPerDay;
                    loadSalesBasedOnProduct(searchWord,startDate,endDate,dataLength);
                    break;
                case "WEEK":
                    startDate =getLongDateAt12(date) - (date.getDay() * millisecondsPerDay + millisecondsPerDay);
                    endDate = getLongDateAt12(date) + ((7-date.getDay())* millisecondsPerDay);
                    loadSalesBasedOnProduct(searchWord,startDate,endDate,dataLength);
                    break;
                default:
                    startDate = getLongDateAt12(new Date(date.getYear(),date.getMonth(),1));
                    endDate = (new Date(date.getYear(),(date.getMonth()+1),1).getTime()) - millisecondsPerDay;
                    loadSalesBasedOnProduct(searchWord,startDate,endDate,dataLength);
                    break;
            }
        }else {
            switch (queryType){
                case "DAY":
                    startDate = getLongDateAt12(date);
                    endDate = getLongDateAt12(date) + millisecondsPerDay;
                    loadNext100Sales(dataLength,startDate,endDate);
                    break;
                case "WEEK":
                    startDate = getLongDateAt12(date) - (date.getDay() * millisecondsPerDay + millisecondsPerDay);
                    endDate = getLongDateAt12(date) + ((7-date.getDay())* millisecondsPerDay);
                    loadNext100Sales(dataLength,startDate,endDate);
                    break;
                default:
                    startDate = new Date(date.getYear(),date.getMonth(),1).getTime();
                    endDate = (new Date(date.getYear(),(date.getMonth()+1),1).getTime()) - millisecondsPerDay;
                    loadNext100Sales(dataLength,startDate,endDate);
                    break;
            }
        }
    }
    private long getLongDateAt12(Date date){
        return new Date(date.getYear(),date.getMonth(),date.getDate()).getTime();
    }





}
