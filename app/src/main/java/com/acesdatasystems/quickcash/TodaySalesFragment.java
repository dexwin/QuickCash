package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acesdatasystems.quickcash.AdapterListeners.SaleItemListener;
import com.acesdatasystems.quickcash.dataataptors.SaleItemAdapter;
import com.acesdatasystems.quickcash.dataataptors.SaleItemComponentsAdapter;
import com.acesdatasystems.quickcash.dataataptors.SaleItemHolder;
import com.acesdatasystems.quickcash.model.AccountUser;
import com.acesdatasystems.quickcash.model.Sale;
import com.acesdatasystems.quickcash.model.SaleItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acesdatasystems.quickcash.BaseActivity.connectionState;
import static com.acesdatasystems.quickcash.BaseActivity.isPrinterConnected;
import static com.acesdatasystems.quickcash.BaseActivity.sendSaleData;

public class TodaySalesFragment extends Fragment implements SaleItemListener {

    private static final String TAG = "TodaySalesFragment";
    Spinner searchCategorySpinner;
    Spinner spSelectWaiter;
    ImageView todaySalesCal;
    List<Sale> salesArrayList;
    SaleItemAdapter saleItemAdapter;
    private String selectedDate;
    RecyclerView salesItemsHolder;
    private Map<Integer,String> monthString;
    TextView tvDay;
    TextView tvTotalSales;
    long todaysDateLong;
    Button btnPrint;
    List<SaleItem> saleItems;
    View rootView;
    Date dateSelected = null;
    AccountUser selectedWaiter= null;
    ArrayList<AccountUser> userArrayList;
    Long selectedDateLong = null;
    ProgressDialog dialog;

    ArrayAdapter<AccountUser> spinnerAdapter;

    public TodaySalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         rootView = inflater.inflate(R.layout.fragment_today_sales, container, false);

        spSelectWaiter = rootView.findViewById(R.id.spSelectWaiter);
        todaySalesCal = rootView.findViewById(R.id.todaySalesCal);
        tvDay = rootView.findViewById(R.id.tvDay);
        tvTotalSales = rootView.findViewById(R.id.tvTotalSales);
        salesArrayList = new ArrayList<>();
        saleItemAdapter = new SaleItemAdapter(salesArrayList);

        spinnerAdapter = new ArrayAdapter<AccountUser>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSelectWaiter.setAdapter(spinnerAdapter);

        AccountUser hintUser = new AccountUser();
        hintUser.setName("Select Waiter");
        hintUser.setUserId("STATIC_ID");
        spinnerAdapter.add(hintUser);
        spSelectWaiter.setSelection(0);

        loadWaitersIntoSpinner();
        Date datei = new Date();
        todaysDateLong = new Date(datei.getYear(),datei.getMonth(),datei.getDate()).getTime();
        dateSelected = new Date(datei.getYear(),datei.getMonth(), datei.getDate());
        selectedDateLong = todaysDateLong;

        Date tommorowsDate = new Date(dateSelected.getYear(),dateSelected.getMonth(), dateSelected.getDate() + 1);
        loadNext100Sales(1000,getLongDateAt12(dateSelected),getLongDateAt12(tommorowsDate));
        SaleItemHolder.setListener(this);
        todaySalesCalOnClick();
        setupMonthString();

        return rootView;
    }



    public void loadDateSales(long date){
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                "Loading Sales Data, Please wait...", true);
        String accountId = LoginActivity.currentAccountUser.getRestaurant();
        Query query = LoginActivity.accountsRef.child(accountId).child("sales").orderByChild("timeSold").startAt(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                salesArrayList.clear();
                saleItemAdapter.notifyDataSetChanged();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Sale sale = snapshot.getValue(Sale.class);
                    salesArrayList.add(sale);
                }
                if(salesArrayList != null){
//            stockItemsHolder.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    salesItemsHolder = rootView.findViewById(R.id.salesItemsHolder);
                    salesItemsHolder.setLayoutManager(mLayoutManager);
                    salesItemsHolder.setItemAnimator(new DefaultItemAnimator());
                    salesItemsHolder.setAdapter(saleItemAdapter);
                    salesItemsHolder.setItemAnimator(new DefaultItemAnimator());
                }
                double total = 0;
                for (Sale sale : salesArrayList){
                    total += sale.getTotCost();
                }
                tvTotalSales.setText(String.valueOf(total));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),"Error Fetching Sales Data", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    //todo barry call this method when loading sale. the resulst should be handled at loadSalesBasesOnPersonnel
    public void loadSalesDetail(String queryType, Date date, String searchWord, String searchField ,int dataLength){
        long startDate;
        long endDate ;
        final long millisecondsPerDay =86400000;
        if  (searchField.toLowerCase().contains("wait")){
            //looading sales based on waiter
            switch (queryType){
                case "DAY":
                    startDate = getLongDateAt12(date) ;
                    endDate = startDate + millisecondsPerDay;
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
                    endDate = startDate + millisecondsPerDay;
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
    public void loadNext100Sales(int offset,long startDate, long endDate){
        dialog = ProgressDialog.show(getContext(), "",
                "Loading Sales Data, Please wait...", true);
        String accountId = LoginActivity.currentAccountUser.getRestaurant();
        Query query = LoginActivity.accountsRef.child(accountId).child("sales").orderByChild("timeSold").startAt(startDate).endAt(endDate).limitToFirst(offset);
        final List<Sale> saleList = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                salesArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Sale sale = (Sale) snapshot.getValue(Sale.class);
                    salesArrayList.add(sale);
                }
                if(salesArrayList != null){
//            stockItemsHolder.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    salesItemsHolder = rootView.findViewById(R.id.salesItemsHolder);
                    salesItemsHolder.setLayoutManager(mLayoutManager);
                    salesItemsHolder.setItemAnimator(new DefaultItemAnimator());
                    salesItemsHolder.setAdapter(saleItemAdapter);
                    salesItemsHolder.setItemAnimator(new DefaultItemAnimator());
                }
                double total = 0;
                for (Sale sale : salesArrayList){
                    total += sale.getTotCost();
                }
                tvTotalSales.setText(String.valueOf(total));
                tvDay.setText(selectedDate);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                if(!connectionState){
                    if(dialog.isShowing()){
                        dialog.dismiss();
                        BaseActivity.showNoNetDialog(getContext());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // todo handle error here
            }
        });

        if(!connectionState){
            if(dialog.isShowing()){
                dialog.dismiss();
                BaseActivity.showNoNetDialog(getContext());
            }
        }


    }


    public void loadSalesBasedOnProduct(String productName, final long startDate,final long endDate ,int lenghtOfData){
        String accountId = LoginActivity.currentAccountUser.getRestaurant();
        Query query = LoginActivity.accountsRef.child(accountId).child("saleItems").orderByChild("itemName").equalTo(productName).limitToFirst(lenghtOfData);
//        .orderByChild("timeSold").startAt(startDate).endAt(endDate).limitToFirst(lenghtOfData);
        final List<SaleItem> saleList = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaleItem sale = (SaleItem) snapshot.getValue(SaleItem.class);
                    if (sale.getTimeSold() > startDate && sale.getTimeSold()<= endDate)
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

    public void loadSalesBasesOnPersonnel(String personellId, final long startDate, final long endDate, int datalenght){
        dialog = ProgressDialog.show(getContext(), "",
                "Loading Sales Data, Please wait...", true);
        String accountId = LoginActivity.currentAccountUser.getRestaurant();
        Query query = LoginActivity.accountsRef.child(accountId).child("sales").orderByChild("personnelId").equalTo(personellId).limitToFirst(datalenght);
//        query.orderByChild("timeSold").startAt(startDate).endAt(endDate).limitToFirst(datalenght);
        final List<Sale> saleList = new ArrayList<>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                salesArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Sale sale = (Sale) snapshot.getValue(Sale.class);
                    if (sale !=null) {
                        if (sale.getTimeSold() >= startDate) {
                            if (sale.getTimeSold() <= endDate) {
                                salesArrayList.add(sale);
                            }
                        }
                    }

                }
                if(salesArrayList != null){
//            stockItemsHolder.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    salesItemsHolder = rootView.findViewById(R.id.salesItemsHolder);
                    salesItemsHolder.setLayoutManager(mLayoutManager);
                    salesItemsHolder.setItemAnimator(new DefaultItemAnimator());
                    salesItemsHolder.setAdapter(saleItemAdapter);
                    salesItemsHolder.setItemAnimator(new DefaultItemAnimator());
                }
                double total = 0;
                for (Sale sale : salesArrayList){
                    total += sale.getTotCost();
                }
                tvTotalSales.setText(String.valueOf(total));
                tvDay.setText(selectedDate);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                if(!connectionState){
                    if(dialog.isShowing()){
                        dialog.dismiss();
                        BaseActivity.showNoNetDialog(getContext());
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(getContext(),"Error Fetching Sales Data", Toast.LENGTH_SHORT).show();
            }

        });

        if(!connectionState){
            if(dialog.isShowing()){
                dialog.dismiss();
                BaseActivity.showNoNetDialog(getContext());
            }
        }

    }

    @Override
    public void saleItemClicked(final Sale item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.sale_list_card,null,false);
        builder.setView(viewInflated);
        Button btnClose = viewInflated.findViewById(R.id.btnClose);
        TextView tvWaiter = viewInflated.findViewById(R.id.tvWaiter);
        TextView tvTotalPrice = viewInflated.findViewById(R.id.tvTotalPrice);
        ListView tableItemListView = viewInflated.findViewById(R.id.tableItemListView);
        Button btnPrint = viewInflated.findViewById(R.id.btnPrint);
        tvTotalPrice.setText("GHS "+String.valueOf(item.getTotCost()));
        tvWaiter.setText(item.getPersonnelName());

        String id = item.getSaleId();
        saleItems = new ArrayList<>();
        final SaleItemComponentsAdapter saleItemComponentsAdapter = new SaleItemComponentsAdapter(getContext(),(ArrayList<SaleItem>)saleItems);
        tableItemListView.setAdapter(saleItemComponentsAdapter);

        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child("saleItems").orderByChild("saleId").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaleItem saleItem = snapshot.getValue(SaleItem.class);
                    saleItems.add(saleItem);
                }
                saleItemComponentsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final AlertDialog alertDialog = builder.create();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPrinterConnected){
                    sendSaleData(item,saleItems,getActivity());
                    alertDialog.dismiss();
                }else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                    View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.device_not_connected,null,false);
                    builder.setView(viewInflated);
                    builder.show();
                    alertDialog.dismiss();
                    builder.setCancelable(true);
                }
            }
        });

        alertDialog.show();
        alertDialog.setCancelable(true);
    }

    public void todaySalesCalOnClick(){
        todaySalesCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.sales_date_calender,null,false);
                builder.setView(viewInflated);
//                Button btnOk = viewInflated.findViewById(R.id.btnOk);
                final CalendarView calendarView = viewInflated.findViewById(R.id.calendarView);

                final AlertDialog alertDialog = builder.create();
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                        int d = dayOfMonth;
//                        int m = month;
//                        int y = year;
//
//                        selectedDate = String.valueOf(d) + " - " + String.valueOf(m) + " - " + String.valueOf(y);
//                          calendar = new GregorianCalendar( year, month, dayOfMonth );
//                          selectedDate = calendar.toString();
                        selectedDate = dayOfMonth+ "-"+monthString.get((month + 1))+"-"+year;
                        String string_date = selectedDate;

                        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
                        try {
                            dateSelected = f.parse(string_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        selectedDateLong = dateSelected.getTime();

                            if(spSelectWaiter.getSelectedItemPosition() == 0){
                                Date tommorowsDate = new Date(dateSelected.getYear(),dateSelected.getMonth(), dateSelected.getDate() + 1);
                                loadNext100Sales(1000,getLongDateAt12(dateSelected),getLongDateAt12(tommorowsDate));
                            }else {
                                loadSalesDetail("DAY",dateSelected,selectedWaiter.getUserId(),"waiter",10000);
                            }

                        alertDialog.dismiss();
                    }
                });


//                btnOk.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(tvDay.getText().toString().equalsIgnoreCase("Today")){
//                            loadDateSales(todaysDateLong);
//                        }else {
//
//                            if(((AccountUser)spSelectWaiter.getSelectedItem()).getUserId().equals("STATIC_ID")){
//                                loadSalesDetail("DAY",dateSelected,"","waiter",10000);
//                            }else {
//                                loadSalesDetail("DAY",dateSelected,selectedWaiter.getUserId(),"waiter",10000);
//                                Toast.makeText(getContext(),dateSelected.toString() + " " +selectedWaiter.getName(),Toast.LENGTH_LONG).show();
//                            }
//
//                        }
//                        alertDialog.dismiss();
//                    }
//                });

                alertDialog.show();
                alertDialog.setCancelable(false);
            }
        });
    }

    public void setupMonthString(){
        monthString = new HashMap<>();
        monthString.put(1,"Jan");
        monthString.put(2,"Feb");
        monthString.put(3,"March");
        monthString.put(4,"April");
        monthString.put(5,"May");
        monthString.put(6,"June");
        monthString.put(7,"July");
        monthString.put(8,"Aug");
        monthString.put(9,"Sept");
        monthString.put(10,"Oct");
        monthString.put(11,"Nov");
        monthString.put(12,"Dec");
    }

    public void loadWaitersIntoSpinner(){

        userArrayList = new ArrayList<>();
        DatabaseReference reference = LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant());
        reference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AccountUser user = snapshot.getValue(AccountUser.class);
                    userArrayList.add(user);
                }

                if(!userArrayList.isEmpty()){
                    for(AccountUser user : userArrayList){
                        spinnerAdapter.add(user);
                    }
                }

                spSelectWaiter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedWaiter = (AccountUser) adapterView.getItemAtPosition(i);
                        if(i == 0){
                            Date tommorowsDate = new Date(dateSelected.getYear(),dateSelected.getMonth(), dateSelected.getDate() + 1);
                            loadNext100Sales(1000,getLongDateAt12(dateSelected),getLongDateAt12(tommorowsDate));
                        }else {
                            loadSalesDetail("DAY",dateSelected,selectedWaiter.getUserId(),"waiter",1000);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //no need to handle this error as user will not directly interact with it's population
            }
        });
    }
}
