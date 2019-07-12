package com.acesdatasystems.quickcash.datasource;

/*
GENERAL IMPORTS
 */
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acesdatasystems.quickcash.AdapterListeners.TableListener;
import com.acesdatasystems.quickcash.AdapterListeners.EditListener;
import com.acesdatasystems.quickcash.BaseActivity;
import com.acesdatasystems.quickcash.R;
import com.acesdatasystems.quickcash.dataataptors.TableItemAdapter;

import java.util.ArrayList;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class Table extends BaseActivity{
    private String tableName = "";
    private double totalAmount = 0.0;
    private  boolean isServing = false;
    private String waiter;
    private ArrayList<TableItem> tableItemList = new ArrayList<>();
    public static TableListener tableListener;
    public static EditListener editListener;
    CircularProgressButton circularProgressButton;
    ImageButton imgBtnEdit;
    LinearLayout tableCardLayout;
    Handler handler;

    public static Table table;
    private View rootView;
    ArrayAdapter tableItemAdapter = null;
    TextView txtTotalPrice;
    ListView tableItemListView;
    Context context;

    /*
    constructor method constructs table object and initialize fields
     */
    public Table (final String tableName, double totalAmount,final boolean isServing, final Context context, LinearLayout parent, ArrayList<TableItem>items) {

        this.setTotalAmount(totalAmount);
        this.setTableName(tableName);
        this.setTableItemList(items);
        rootView = LayoutInflater.from(context).inflate(R.layout.table_card, parent, false);
        TextView txtTableName = rootView.findViewById(R.id.txtTableName);
         txtTotalPrice = rootView.findViewById(R.id.txtTotalPrice);
         tableItemListView = rootView.findViewById(R.id.tableItemListView);
        circularProgressButton = rootView.findViewById(R.id.btnPrintOrder);
        imgBtnEdit = rootView.findViewById(R.id.imgBtnEdit);

        // method toggles visibility of table controls
        if(tableName.toLowerCase().contains("quick")){
            circularProgressButton.setVisibility(View.GONE);
            imgBtnEdit.setVisibility(View.GONE);
        }

        final LinearLayout rootViewContainer = rootView.findViewById(R.id.tableCard);
        final TextView txtLongPress = rootView.findViewById(R.id.txtLongPress);

         tableCardLayout = rootView.findViewById(R.id.tableCardLayout);
         txtLongPress.setVisibility(View.GONE);
            if(isServing){
            txtLongPress.setVisibility(View.GONE);
            }
        txtTableName.setText(tableName);
        txtTotalPrice.setText(String.valueOf(totalAmount));

        tableItemAdapter = new TableItemAdapter(context, items);
        tableItemListView.setAdapter(tableItemAdapter);

        handler = new Handler(context.getMainLooper());

        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tableItemList.isEmpty()){
                    if(isPrinterConnected){
                        circularProgressButton.startAnimation();
                        BaseActivity.printOrder(waiter,tableName,tableItemList,context);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            circularProgressButton.doneLoadingAnimation(Color.parseColor("#333d39"), BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_done_white_48dp));
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }else {
                        Toast.makeText(context, "Printer Disconnected", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(context, "No Orders on table", Toast.LENGTH_SHORT).show();
                }

            }
        });

        /*
        onClick listener for Edit Button
         */
        imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tableItemList.isEmpty()){
                    Toast.makeText(context,"No Orders to edit", Toast.LENGTH_SHORT).show();
                }else {
                    editListener.editTapped(rootView);
                }
            }
        });

        rootViewContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                txtLongPress.setVisibility(View.GONE);
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50); // 50 is time in ms
                tableListener.tableTapped(rootView);
                return false;
            }
        });
    }
    /*
       constructor overload method constructs table object and initialize fields
     */
    public Table (final String tableName, double totalAmount,final boolean isServing, final Context context, LinearLayout parent) {

        this.setTotalAmount(totalAmount);
        this.setTableName(tableName);
        this.setTableItemList(tableItemList);
        rootView = LayoutInflater.from(context).inflate(R.layout.table_card, parent, false);
        TextView txtTableName = rootView.findViewById(R.id.txtTableName);
        txtTotalPrice = rootView.findViewById(R.id.txtTotalPrice);
        tableItemListView = rootView.findViewById(R.id.tableItemListView);
        circularProgressButton = rootView.findViewById(R.id.btnPrintOrder);
        imgBtnEdit = rootView.findViewById(R.id.imgBtnEdit);

        if(tableName.toLowerCase().contains("quick")){
            circularProgressButton.setVisibility(View.GONE);
            imgBtnEdit.setVisibility(View.GONE);
        }

        final LinearLayout rootViewContainer = rootView.findViewById(R.id.tableCard);
        final TextView txtLongPress = rootView.findViewById(R.id.txtLongPress);
        tableCardLayout = rootView.findViewById(R.id.tableCardLayout);
        if(isServing){
            txtLongPress.setVisibility(View.GONE);
        }
        txtTableName.setText(tableName);
        txtTotalPrice.setText(String.valueOf(totalAmount));

        tableItemAdapter = new TableItemAdapter(context, tableItemList);
        tableItemListView.setAdapter(tableItemAdapter);

        handler = new Handler(context.getMainLooper());


        /*
        circular progress button onclick listener
         */
        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!tableItemList.isEmpty()){
                    if(isPrinterConnected){
                        circularProgressButton.startAnimation();
                        BaseActivity.printOrder(waiter,tableName,tableItemList,context);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            circularProgressButton.doneLoadingAnimation(Color.parseColor("#333d39"), BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_done_white_48dp));
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }else {
                        Toast.makeText(context, "Printer Disconnected", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(context, "No Orders on table", Toast.LENGTH_SHORT).show();
                }

            }
        });
        /*
        set onclick listener for edit table control
         */

        imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tableItemList.isEmpty()){
                    Toast.makeText(context,"No Orders to edit", Toast.LENGTH_SHORT).show();
                }else {
                    editListener.editTapped(rootView);
                }
            }
        });

        rootViewContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                txtLongPress.setVisibility(View.GONE);
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(50); // 50 is time in ms
                tableListener.tableTapped(rootView);
                return false;
            }
        });
    }

     /*
       constructor overload method constructs table object and initialize fields
     */

    public Table(String tableName, double totalAmount) {
        this.tableName = tableName;
        this.totalAmount = totalAmount;
    }


    /*
    refresh table method reloads table data to update on edit
     */
    public void refreshTable(){
        double totalPrice = 0.0;
        for(TableItem i: tableItemList){
            totalPrice += i.getPrice() * i.getQuantity();
        }
        this.setTotalAmount(totalPrice);
        txtTotalPrice.setText(String.valueOf(totalPrice));
        tableItemAdapter.notifyDataSetChanged();
    }

    /*
    TABLE SETTERS AND GETTERS
     */
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ArrayList<TableItem> getTableItemList() {
        return tableItemList;
    }

    public void setTableItemList(ArrayList<TableItem> tableItemList) {
        this.tableItemList = tableItemList;
    }

    public boolean isServing() {
        return isServing;
    }

    public String getWaiter() {
        return waiter;
    }

    public void setServing(boolean serving){
        isServing = serving;
    }

    public void setWaiter(String waiter) {
        this.waiter = waiter;
    }

    public View getRootView() {
        return rootView;
    }

    public ArrayAdapter getTableItemAdapter() {
        return tableItemAdapter;
    }

    public static Table getRootView(final String tableName, double totalAmount,final boolean isServing, final Context context, LinearLayout parent,ArrayList<TableItem> items) {
        table = new Table(tableName,totalAmount,isServing,context,parent, items);
        table.getRootView().setTag(table);
        return table;
    }
    public static Table getRootView(final String tableName, double totalAmount,final boolean isServing, final Context context, LinearLayout parent) {
        table = new Table(tableName,totalAmount,isServing,context,parent);
        table.getRootView().setTag(table);
        return table;
    }
    public static void setTableListener(TableListener listener) {
        tableListener = listener;
    }

    public static void setEditListener(EditListener listener){
        editListener = listener;
    }

    public LinearLayout getTableCardLayout() {
        return tableCardLayout;
    }

    public void setTableCardLayout(LinearLayout tableCardLayout) {
        this.tableCardLayout = tableCardLayout;
    }
}
