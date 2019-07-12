package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acesdatasystems.quickcash.AdapterListeners.DeletePositionListener;
import com.acesdatasystems.quickcash.AdapterListeners.EditListener;
import com.acesdatasystems.quickcash.AdapterListeners.TableListener;
import com.acesdatasystems.quickcash.dataataptors.TableItemEditAdapter;
import com.acesdatasystems.quickcash.datasource.Table;
import com.acesdatasystems.quickcash.datasource.TableItem;
import com.acesdatasystems.quickcash.model.AccountUser;
import com.acesdatasystems.quickcash.model.KitchenProduct;
import com.acesdatasystems.quickcash.model.RestHeader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static com.acesdatasystems.quickcash.ProductsActivity.KITCHEN_LOCATION;
import static com.acesdatasystems.quickcash.ProductsActivity.kitchenItemsListener;

//printing imports


public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, TableListener, EditListener{

    // will show the statuses like bluetooth open, close or data sent
    public static TextView lblPrinterState;

    // will enable user to enter any text to be printed


    private static final String PRINTER_STATE_DISCONNECTED = "Printer State Disconnected";
    private static final String PRINTER_STATE_CONNECTED = "Bluetooth Printer Connected";

    private TextView txtNavUsername;
    private TextView txtNavUsermail;
    private ImageView imgProfileImage;
    private static FloatingActionButton fab1, fab2, fab3,fab;
    private Boolean
            isFABOpen = false;
    static Table selectedTable = null;
    private Menu navMenu;

    FrameLayout fabLayout;
    private static ImageView imgSetupPrinter;
    static LinearLayout tableCardHolder;
    List<Table> tableList;
    BluetoothReceiver bluetoothReceiver;
    String selectedOption;
    private  static  FileOutputStream fileOutputStream;
    private static FileInputStream fileInputStream;
    public   final String FILE_NAME = "sales.json";
    Boolean isTableExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasApplicationStarted = true;
        try {
            fileOutputStream = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             fileInputStream= openFileInput(FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            updateSaleListOnLocalDisk();
        }

        //Loading saved tables from precious login



        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tableCardHolder = findViewById(R.id.tableCardHolder);
        downloadProfile();
        tableList = new ArrayList<>();
        isPrinterConnected = false;

        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter brFilter = new IntentFilter();
        brFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        brFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, brFilter);

        createNewTable("Quick Table");
        loadingKitchenProducts();
        Table.setTableListener(this);
        Table.setEditListener(this);
//        downloadImage();

        fab = findViewById(R.id.fab);
        fab1 =  findViewById(R.id.fab1);
        fab2 =  findViewById(R.id.fab2);
        fab3 =  findViewById(R.id.fab3);
        fabLayout = findViewById(R.id.fabMenu);
        lblPrinterState = findViewById(R.id.lblPrinterState);
        imgSetupPrinter = findViewById(R.id.imgSetupPrinter);
        imgSetupPrinter.setBackgroundResource(R.drawable.printer_wireless_disconnect_48x48);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBTBroadcastReceiver, filter);


        fab.setOnClickListener(new View.OnClickListener() {
            String tableName ="";
            @Override
            public void onClick(View view) {
               // create new table

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.text_input,null,false);
                final EditText input = viewInflated.findViewById(R.id.table_name_input);
                builder.setView(viewInflated);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        tableName = input.getText().toString();
                        if(tableName.length()<1){
                            Toast.makeText(HomeActivity.this, "Table Not Created, Enter Table Name", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!isFABOpen) {
                                    if (isTableExist(tableName)) {
                                        Toast.makeText(HomeActivity.this, "Table Already Exists", Toast.LENGTH_SHORT).show();
                                        isTableExist = true;
                                    } else {
                                        isTableExist = false;
                                    }
                                if (!isTableExist) {
                                        if(isFABOpen){
                                            closeFABMenu();
                                            createNewTable(tableName);
                                        }else {
                                            createNewTable(tableName);
                                        }
                                }
                            }
                        }

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }

        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeTable(selectedTable);
                processKitchenProduct(selectedTable);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            // send data typed by the user to be printed
                public void onClick(View v) {
                    processPayment();
                }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent productIntent = new Intent(HomeActivity.this, ProductsActivity.class);
                selectedTable.setServing(true);
                startActivity(productIntent);
                closeFABMenu();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navMenu = navigationView.getMenu();

        View header= navigationView.getHeaderView(0);
        txtNavUsername = header.findViewById(R.id.txtNavUsername);
        txtNavUsermail = header.findViewById(R.id.txtNavUsermail);
        imgProfileImage = header.findViewById(R.id.imgProfileImage);
        txtNavUsername.setText(LoginActivity.currentAccountUser.getName());
        txtNavUsermail.setText(LoginActivity.currentAccountUser.getEmail());

        createFirstTable();

        setupNavDrawer(LoginActivity.currentAccountUser);

        //calling method to load saved json sales files
//        loadSavedSDates();
        listOfSavedSales.clear();
        getSavedGson();
        sendPost();


        // initialize HashMap in BaseActivity and load all products
//        productsMap = new HashMap<>();
//        if(productsMap.isEmpty()){
//            loadDrinks();
//            loadFood();
//            loadDessert();
//            loadDProducts();
//        }


        kitchenItemsListener();

    }

    public static void printerConnected(){
        isPrinterConnected = true;
        imgSetupPrinter.setBackgroundResource(R.drawable.printer_wireless_connect_48x48);
        lblPrinterState.setText(PRINTER_STATE_CONNECTED);
    }
    public static void printerDisconnected(){
        isPrinterConnected = false;
        imgSetupPrinter.setBackgroundResource(R.drawable.printer_wireless_disconnect_48x48);
        lblPrinterState.setText(PRINTER_STATE_DISCONNECTED);
    }
    public void setupNavDrawer(AccountUser user){

        Bitmap userAvater = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.default_avatar);

        if(LoginActivity.currentAccountUser.getProfilePic() == null){
            LoginActivity.currentAccountUser.setProfilePic(userAvater);
            imgProfileImage.setImageBitmap(LoginActivity.currentAccountUser.getProfilePic());
        }else {
            imgProfileImage.setImageBitmap(LoginActivity.currentAccountUser.getProfilePic());
        }


        if(user.getAccountType().toLowerCase().equalsIgnoreCase("manager")){
            SubMenu subMenuManagement;
            subMenuManagement = navMenu.addSubMenu("Management");


            subMenuManagement.add(1, 1, Menu.FIRST,"Sales")
                    .setIcon(R.drawable.chart_bar);
            subMenuManagement.add(2, 2, Menu.FIRST,"Stock")
                    .setIcon(R.drawable.finance);
            subMenuManagement.add(3, 3, Menu.FIRST,"Users")
                    .setIcon(R.drawable.account_supervisor);
//            subMenuManagement.add(5, 5, Menu.FIRST,"Upgrade")
//                    .setIcon(R.drawable.arrow_up_box);

            SubMenu subMenuAccount;
            subMenuAccount = navMenu.addSubMenu("Account");

            subMenuAccount.add(4, 4, Menu.FIRST,"Account Info")
                    .setIcon(R.drawable.alert_circle);
            subMenuAccount.add(6, 6, Menu.FIRST,"Account Settings")
                    .setIcon(R.drawable.settings);
        }else{
        SubMenu subMenuAccount;
        subMenuAccount = navMenu.addSubMenu("Account");

        subMenuAccount.add(4, 4, Menu.FIRST,"Account Info")
                .setIcon(R.drawable.alert_circle);
        subMenuAccount.add(6, 6, Menu.FIRST,"Account Settings")
                .setIcon(R.drawable.settings);
        }


    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    public  void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return super.onTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }else {
               Intent startMain = new Intent(Intent.ACTION_MAIN);
               startMain.addCategory(Intent.CATEGORY_HOME);
               startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(startMain);
            }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent navIntent;

        if(id == 1){
            navIntent = new Intent(getApplicationContext(),SalesActivity.class);
            startActivity(navIntent);

        }else if(id == 2){
            navIntent = new Intent(getApplicationContext(),StockActivity.class);
            startActivity(navIntent);
        }else if(id == 4){
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.account_info,null,false);
            builder.setView(viewInflated);
            Button btnUpgrade = viewInflated.findViewById(R.id.btnUpgrade);
            TextView tvAccountName = viewInflated.findViewById(R.id.tvAccountName);
            TextView tvCurrentSubscription = viewInflated.findViewById(R.id.tvCurrentSubscription);
            if (LoginActivity.currentAccount !=null){
                tvAccountName.setText(LoginActivity.currentAccount.getBusinessName());
                tvCurrentSubscription.setText(LoginActivity.subscriptionDetails.getPlan().toLowerCase());
            }
//            tvCurrentSubscription.setText(LoginActivity.currentAccountUser.getRestaurant());
            final AlertDialog alertDialog = builder.create();

            btnUpgrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
            alertDialog.setCancelable(true);
        }else if(id == 5){
            Intent upgradeActivity = new Intent(HomeActivity.this, UpgradeActivity.class);
            startActivity(upgradeActivity);
        }else if(id == 3){
            Intent usersActivity = new Intent(HomeActivity.this, UsersActivity.class);
            startActivity(usersActivity);
        }else if(id == 6){
            Intent profileSetupActivity = new Intent(HomeActivity.this, ProfileSetupActivity.class);
            profileSetupActivity.putExtra("Source","Home");
            startActivity(profileSetupActivity);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void createFirstTable(){
            Table table = new Table("Quick Table",0.00);
            TableItem tableItem = new TableItem("Long Press To Serve",0.0,false);
            table.getTableItemList().add(tableItem);
    }

    @Override
    public void tableTapped(View table) {
        if(tableCardHolder.getChildCount() != 0){
            for (int i = 0; i < tableCardHolder.getChildCount(); i++) {
                tableCardHolder.getChildAt(i).setPadding(0,0,0,0);
            }
        }
        selectedTable = (Table) table.getTag();
        selectedTable.getTableCardLayout().setPadding(15,0,0,0);
        showFABMenu();
    }
    public void removeTable(final Table selectedTable) {
        if(selectedTable.getTableName().equalsIgnoreCase("Quick Table")){
           if(selectedTable.getTableItemList().size() > 0){
               selectedTable.getTableItemList().clear();
               selectedTable.getTableItemAdapter().notifyDataSetChanged();
               closeFABMenu();
           }else {
               Toast.makeText(getApplicationContext(), "Quick Table Cannot be Cleared", Toast.LENGTH_LONG).show();
               closeFABMenu();
           }

        }
        else{
            final View selectedView = selectedTable.getRootView();
            selectedView.animate().translationXBy(-2000).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    ((ViewManager)selectedTable.getRootView().getParent()).removeView(selectedView);
                     closeFABMenu();
                }
            });

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            if(isFABOpen){
                Rect outRect = new Rect();
                fabLayout.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())){
                    closeFABMenu();
                    return false;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mBTBroadcastReceiver);
        isPrinterConnected = null;
        isBTEnabled = null;
        isBTPrinterFound = null;
        isFABOpen = null;
        isTableExist = null;
        unregisterReceiver(bluetoothReceiver);
    }

//     this will send text data to be printed by the bluetooth printer

    public static boolean sendData(AccountUser currentUser ,List<TableItem> tableItems,Context context) throws IOException {

        double totalAmount = 0.0;
        if(tableItems.size() > 0){
                for (TableItem t: tableItems) {
                    totalAmount += t.getPrice() * t.getQuantity();
                }
            try {
                BILL = new StringBuilder("\n"+LoginActivity.currentAccount.getBusinessName()+
                        "\n"+LoginActivity.currentAccount.getLocation()+
                        "\n"+LoginActivity.currentAccount.getMobileNumber()+
                        "\nTable #: " + selectedTable.getTableName() +
                        "\nWaiter : " + currentUser.getName() +
                        DIVIDER +
                        String.format("%-20s%10s%10s","Product","Qty","Price" )+
                        DIVIDER);

                for(TableItem item : tableItems){
                    BILL.append(String.format("\n%-20s%10s%10s",item.getProductName(),item.getQuantity(), item.getPrice()));
                }


                BILL.append(new StringBuilder(DIVIDER).append(
                        new StringBuilder("Order Total").append(new StringBuilder(
                                DIVIDER.replaceAll("-", " ").replace(
                                        DIVIDER.replaceAll("-", " ").substring(
                                                0, 12), "")).append(
                                new StringBuilder("GHS ").append(totalAmount))).append(
                                new StringBuilder(DIVIDER)).append(
                                "Thank You. Hope You Enjoyed Our Service!\nReceipt Issued at : "+getCurrentTimeStamp()+"\n")));

                BILL.append(new StringBuilder(DIVIDER).append("Powered by QuickCashPOS " +
                        "Tel: 0302434633 / 0209044390\n\n\n\n"));
                mmOutputStream.write(BILL.toString().getBytes());

                // tell the user data were sent
                Toast.makeText(context,"Printing...",Toast.LENGTH_LONG);
                lblPrinterState.setText("Data sent.");
                lblPrinterState.setText(PRINTER_STATE_CONNECTED);
                saveList(new RestHeader().createHeader(tableItems,LoginActivity.currentAccountUser,0.00,totalAmount));
                //todo dompreh put your codes for saving sale

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(context.getApplicationContext(),"No Products Added, Print Failed",Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

//     close the connection to bluetooth printer.

    private final BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        lblPrinterState.setText("Bluetooth Turned Off");
                            closeBT();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        lblPrinterState.setText("Bluetooth Ready");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:

                        break;
                }

            }
        }
    };

    public void createNewTable(String tableName){
        Table quickTable = Table.getRootView(tableName,0.0,false,getApplicationContext(),tableCardHolder);
        tableCardHolder.addView(quickTable.getRootView());
    }
    public boolean isTableExist(String tableName){
        final int childCount = tableCardHolder.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = tableCardHolder.getChildAt(i);
            if(((Table)v.getTag()).getTableName().equalsIgnoreCase(tableName)){
                return true;
            }
        }
        return false;
    }

    public static void refreshTable(Table refreshedTable){
        int index = tableCardHolder.indexOfChild(selectedTable.getRootView());
        tableCardHolder.removeViewAt(index);
        tableCardHolder.addView(refreshedTable.getRootView(),index);
        refreshedTable.getTableCardLayout().setPadding(15,0,0,0);

    }


    public void processPayment() {

        if (isPrinterConnected) {

            selectedOption = "cash";
            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.payment_options, null, false);
            builder.setView(viewInflated);

            final ImageView cash = viewInflated.findViewById(R.id.action_cash);
            final ImageView card = viewInflated.findViewById(R.id.action_card);
            final ImageView momo = viewInflated.findViewById(R.id.action_mobile_money);

            Button checkout = viewInflated.findViewById(R.id.btnCheckout);
            final CheckBox cbIssueSaleReceipt = viewInflated.findViewById(R.id.cbIssueSaleReceitp);
            cbIssueSaleReceipt.setChecked(true);
            final TextView selectedTitle = viewInflated.findViewById(R.id.tvSelectedOption);

            cash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedOption = "cash";
                    selectedTitle.setText("Cash");
                    cash.setBackgroundResource(R.drawable.material_selection_background);
                    momo.setBackgroundResource(R.color.colorPrimary);
                    card.setBackgroundResource(R.color.colorPrimary);
                }
            });
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedOption = "card";
                    selectedTitle.setText("Card");
                    card.setBackgroundResource(R.drawable.material_selection_background);
                    momo.setBackgroundResource(R.color.colorPrimary);
                    cash.setBackgroundResource(R.color.colorPrimary);
                }
            });
            momo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedOption = "momo";
                    selectedTitle.setText("Mobile Money");
                    momo.setBackgroundResource(R.drawable.material_selection_background);
                    cash.setBackgroundResource(R.color.colorPrimary);
                    card.setBackgroundResource(R.color.colorPrimary);
                }
            });

            final AlertDialog localDialog = builder.create();
            builder.setCancelable(true);

            checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (selectedOption) {
                        case "cash":
                            // print cash receipt
                            if(cbIssueSaleReceipt.isChecked()){
                                try {
                                    if(sendData(LoginActivity.currentAccountUser, selectedTable.getTableItemList(),HomeActivity.this)){
                                        removeTable(selectedTable);
                                        //removing product from kitchen products
                                        processKitchenProduct(selectedTable);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(), "Sales Recorded", Toast.LENGTH_LONG).show();
                                saveList(new RestHeader().createHeader(selectedTable.getTableItemList(),LoginActivity.currentAccountUser,0.00,selectedTable.getTotalAmount()));
                                removeTable(selectedTable);
                                // TODO: 1/23/2019 Dompreh save sales to firebase
                            }

                            break;
                        case "card":
                            // print card receipt

                            Toast.makeText(HomeActivity.this, "Upgrade ", Toast.LENGTH_SHORT).show();
                            break;
                        case "momo":
                            // print momo receipt

                            Toast.makeText(HomeActivity.this, "Upgrade ", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    localDialog.dismiss();
                }
            });

            localDialog.show();
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.device_not_connected,null,false);
            builder.setView(viewInflated);

            builder.show();
            builder.setCancelable(true);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if(isFABOpen){
                        closeFABMenu();
                    }
                }
            });
        }
    }

    public void processKitchenProduct(final Table selectedTable){
        String kitchenProductToken =KitchenProduct.orderTableKitchenIdList.get(selectedTable.getTableName());

        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child(KITCHEN_LOCATION).child(kitchenProductToken).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                KitchenProduct.orderTableKitchenIdList.remove(selectedTable.getTableName());
            }
         });
    }

    boolean onLoad = true;
    public  void loadingKitchenProducts(){
        final List<KitchenProduct> kitchenProducts = new ArrayList<>();
        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child(KITCHEN_LOCATION).orderByChild("waitressName").equalTo(LoginActivity.currentAccountUser.getName())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (onLoad) {
                            onLoad = false;

                            kitchenProducts.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                KitchenProduct kitchenProduct = snapshot.getValue(KitchenProduct.class);
                                KitchenProduct.orderTableKitchenIdList.put(kitchenProduct.getTableNumber(), snapshot.getKey());
                                kitchenProducts.add(kitchenProduct);
                            }

                            //todo BARRY loading of kitchen stored products in here use kitchenProduct.getTableItems() to get all the table items,
                            // you can use attributes like table name to recreate the table
                            if (!kitchenProducts.isEmpty()) {
                                Table loadedTable;
                                ArrayList<TableItem> tableItems;
                                double total;
                                for (KitchenProduct k : kitchenProducts) {
                                    tableItems = new ArrayList<>();
                                    total = 0.0;
                                    if (k != null) {
                                        for (TableItem tableItem : k.getTableItems()){
                                            tableItems.add(tableItem);
                                            total += tableItem.getQuantity() * tableItem.getPrice();
                                        }
                                        loadedTable = Table.getRootView(k.getTableNumber(), total, false, getApplicationContext(), tableCardHolder,tableItems);
                                        loadedTable.setTableItemList(tableItems);
                                        tableCardHolder.addView(loadedTable.getRootView());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return;
    }

//   public void downloadImage(){
//       String imageRef = String.format("userProfiles/%s.jpeg", LoginActivity.currentAccountUser.getUserId());
//       LoginActivity.mStorageRef.child(imageRef).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//           @Override
//           public void onSuccess(Uri uri) {
//               // Got the download URL for 'users/me/profile.png'
//               decodeUri(uri);
//           }
//       }).addOnFailureListener(new OnFailureListener() {
//           @Override
//           public void onFailure(@NonNull Exception exception) {
//               // Handle any errors
//           }
//       });
//   }



    //Download method
    public void downloadProfile(){
        String imageRef = "";
        if(LoginActivity.currentAccountUser.getUserId() != null){
            imageRef = String.format("userProfiles/%s.jpeg", LoginActivity.currentAccountUser.getUserId());

        }
        StorageReference islandRef = LoginActivity.mStorageRef.child(imageRef);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed

                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                LoginActivity.currentAccountUser.setProfilePic(new BitmapDrawable(bis).getBitmap());
                imgProfileImage.setImageBitmap(LoginActivity.currentAccountUser.getProfilePic());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //todo Handle any errors
                Intent home = new Intent( HomeActivity.this,HomeActivity.class);
                startActivity(home);
                finish();

            }
        });
    }

    public volatile static List<JSONObject>  listOfSavedSales = new ArrayList<>();

    public static void saveList(RestHeader restHeader){
        Gson gson = new Gson();
        JSONObject jsonParam = new JSONObject();
        try {
             jsonParam.putOpt("saleInfo", gson.toJson(restHeader.getSaleInfo()));
             jsonParam.putOpt("saleItems", gson.toJson(restHeader.getSaleItems()));
             jsonParam.putOpt("senderDetails", gson.toJson(restHeader.getSenderDetails()));
            listOfSavedSales.add(jsonParam);
             updateSaleListOnLocalDisk();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void updateSaleListOnLocalDisk(){
        try{


            String fileContents = saleStringBuilder();
            FileOutputStream outputStream;
            outputStream =  fileOutputStream;
            outputStream.write(fileContents.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return the jsong string format of the file
     */
    public static String saleStringBuilder(){
        String finalGson = new Gson().toJson(listOfSavedSales);
        System.out.println(finalGson);
        return finalGson;
    }

    public void getSavedGson(){
        String json;
            try {
                InputStream inputStream = fileInputStream;
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                json = new String(buffer, "UTF-8");
                System.out.println(json);
                if (!json.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getJSONObject(i) != null) {
                            System.out.println(jsonArray.get(i));
                            listOfSavedSales.add(jsonArray.getJSONObject(i));
                        }
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void sendPost(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    waitForSaleitem();
                    if (httpRequesst(listOfSavedSales.get(0))) {
                        if (listOfSavedSales.size() >0 ) listOfSavedSales.remove(listOfSavedSales.get(0));
                        updateSaleListOnLocalDisk();
                    }
                }

            }
        });
        thread.start();
    }

    public void waitForSaleitem(){
        boolean loop = true;
        while (loop){
            if (listOfSavedSales.size() > 0) loop = false;
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean httpRequesst(JSONObject jsonParam){
        try{
            System.out.println(jsonParam);
            String query = jsonParam.toString().replace("\"{", "{").replace("}\"","}");
            query  = query.replace("\\", "").replace("\"[","[").replace("]\"","]");
            Log.i("JSON", query);
            HttpPost httpPost = new HttpPost("http://us-central1-quick-cash-32ecf.cloudfunctions.net/sell");
            StringEntity entity = new StringEntity(query);
            httpPost.setHeader("Content-type","Application/json");
            httpPost.setEntity(entity);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            BasicResponseHandler responseHandler = new BasicResponseHandler();



            httpClient.execute(httpPost, responseHandler);
            return  true;

        }catch (HttpResponseException e) {
            e.printStackTrace();
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }  catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }
    }

    @Override
    public void editTapped(View table) {

        if(tableCardHolder.getChildCount() != 0){
            for (int i = 0; i < tableCardHolder.getChildCount(); i++) {
                tableCardHolder.getChildAt(i).setPadding(0,0,0,0);
            }
        }
        selectedTable = (Table) table.getTag();
        selectedTable.getTableCardLayout().setPadding(15,0,0,0);


        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.table_item_edit_dialog,null,false);
        TextView tableName = viewInflated.findViewById(R.id.table_name);
        TextView totalCost = viewInflated.findViewById(R.id.totalCost);
        final ListView lvEditList = viewInflated.findViewById(R.id.lv_edit_list);
        Button btnUpdate = viewInflated.findViewById(R.id.btnUpdate);

        final TableItemEditAdapter adapter = new TableItemEditAdapter(this, selectedTable.getTableItemList());
        lvEditList.setAdapter(adapter);

        tableName.setText(selectedTable.getTableName());
        totalCost.setText(String.valueOf(selectedTable.getTotalAmount()));
        builder.setView(viewInflated);

        final AlertDialog dialog = builder.create();

        TableItemEditAdapter.setDeletePositionListener(new DeletePositionListener() {
            @Override
            public void deleteTapped(int pos) {
                selectedTable.getTableItemList().remove(pos);
                selectedTable.refreshTable();
                adapter.notifyDataSetChanged();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTable.refreshTable();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCancelable(true);
    }
}
