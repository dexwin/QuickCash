package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS FOR BASEACTIVITY
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.acesdatasystems.quickcash.datasource.TableItem;
import com.acesdatasystems.quickcash.datasource.TotProduct;
import com.acesdatasystems.quickcash.model.ProductDetails;
import com.acesdatasystems.quickcash.model.Sale;
import com.acesdatasystems.quickcash.model.SaleItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static android.app.Service.START_STICKY;
import static com.acesdatasystems.quickcash.LoginActivity.connectedRef;
import static com.acesdatasystems.quickcash.LoginActivity.productsRef;
import static com.acesdatasystems.quickcash.LoginActivity.totProductsRef;

/*////////////
        *
        * // CLASS_NAME : BASEACTIVITY
        *
        * // GENERAL_OVERVIEW : WRITTEN AS FOUNDATION CLASS ON TOP OF WHICH OTHER MODOULS EXTEND */

public class BaseActivity extends AppCompatActivity {

    // bluetooth module imports
    BluetoothAdapter mBluetoothAdapter; // import for bluetooth adapter
    BluetoothSocket mmSocket;   // import for bluetooth socket
    BluetoothDevice mmDevice;   // import for bluetooth device

    // needed for communication to bluetooth device / network
    static OutputStream mmOutputStream;
    static InputStream mmInputStream;
    static Thread workerThread;

    static StringBuilder BILL;
    static final String DIVIDER = "\n-------------------------------" +
            "-----------------\n";


    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    protected Boolean isBTPrinterFound = false;
    protected Boolean isBTEnabled = false;
    public static Boolean isPrinterConnected = false;

        ProgressDialog dialog;

    ListView lstPairedDevices;
    List<String> pairedDevicesList;
    ArrayAdapter pairedDevicesAdapter;
    private AlertDialog printersAlertDialog;
    static Menu actionBarMenu;
    static Boolean isSalesActivityStarted = false;
    static Boolean isProductActivityStarted = false;
    public static boolean hasApplicationStarted = false;


    final int BT_REQUEST = 0;
    static Map<String,ArrayList> productsMap;
    static boolean connectionState = false;
    Handler mHandler;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        actionBarMenu = menu;
        inflater.inflate(R.menu.home, actionBarMenu);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBTBroadcastReceiver, filter);
        onStartCommand();
//        if(productsMap == null){
            productsMap = new HashMap<>();
            loadDessert();
            loadFood();
            loadDrinks();
            loadDProducts();
//        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter!= null){
            if(mBluetoothAdapter.isEnabled()){
                isBTEnabled = true;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            final ProgressDialog dialog = ProgressDialog.show(BaseActivity.this, "",
                    "Logging Out...", true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                            hasApplicationStarted = false;
                            startActivity(loginActivity);
                            closeBT();
                            finishAffinity();

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }else if(id==R.id.action_connect){
            if(isPrinterConnected){
                Toast.makeText(getApplicationContext(), "Device Connected", Toast.LENGTH_SHORT).show();
            }else {
                if(isBTEnabled) {
                    connectPrinter();
                }else {
                    if(!mBluetoothAdapter.isEnabled()) {
                        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBluetooth, BT_REQUEST);
                    }

                }

            }
        }else if(id == android.R.id.home){
            super.onBackPressed();
        }else if(id == R.id.action_disconnect){
                if (isPrinterConnected) {
                    dialog = ProgressDialog.show(BaseActivity.this, "",
                            "Disconnecting Printer, Please wait...", true);

                    closeBT();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5500);
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    Toast.makeText(getApplicationContext(),"Printer Disconnected",Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// Check which request we're responding to
        if (requestCode == BT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                connectPrinter();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mBTBroadcastReceiver);

    }

    // this will find a bluetooth printer device
    void findBT(String deviceName) {
        try {

            if(mBluetoothAdapter == null) {
//                lblPrinterState.setText("No bluetooth device available");
            }
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    if (device.getName().equals(deviceName)) {
                        mmDevice = device;
                        break;
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //     tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            if(mmSocket.isConnected()){
                isBTPrinterFound = true;
            }
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();


        } catch (Exception e) {
            isBTPrinterFound = false;
            e.printStackTrace();
        }
    }

    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectPrinter(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        pairedDevicesList = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice d : pairedDevices) {
                pairedDevicesList.add(d.getName()+"                 " +d.getAddress());
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bonded_device_list, null, false);
        pairedDevicesAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, pairedDevicesList);
        lstPairedDevices = viewInflated.findViewById(R.id.lstPairedDevices);
        lstPairedDevices.setAdapter(pairedDevicesAdapter);

        builder.setView(viewInflated);

        printersAlertDialog = builder.create();
        printersAlertDialog.show();

        lstPairedDevices.setItemsCanFocus(true);

        lstPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String[] printerNameSplit = pairedDevicesList.get(i).split(" ");
                String printerName = printerNameSplit[0];
                printersAlertDialog.dismiss();

                if (isBTEnabled) {
                    dialog = ProgressDialog.show(BaseActivity.this, "",
                            "Connecting to " + printerName + ". Please wait...", true);
                }

                 new BTPrint().execute(printerName);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(6000);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }



    private final BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        isBTEnabled = false;
                            closeBT();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        isBTEnabled = true;
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:

                        break;
                }

            }
        }
    };

    void closeBT(){

        if (mmSocket != null) {
            try  {
                mmSocket.close();
            }   catch (Exception e)  {
                Log.e("Exception", "while closing socket"+e.toString());
            } mmSocket    = null;
        }

        if (mmOutputStream != null) {
            try  {
                mmOutputStream.close();
            }   catch (Exception e)  {
                Log.e("Exception", "while closing outstream"+e.toString());
            } mmOutputStream = null;
        }
        if (mmInputStream  != null) {
            try  {
                mmInputStream.close();
            }   catch (Exception e)  {
                Log.e("Exception", "while closing inputstream"+e.toString());
            } mmInputStream  = null;
        }
        mmDevice = null;
    }


    private class BTPrint extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... devices) {
            try {
                findBT(devices[0]);
                openBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {

            if(isBTPrinterFound){
                dialog.dismiss();
            }else if(isBTEnabled){
                AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.device_no_available,null,false);
                builder.setView(viewInflated);
                builder.show();
            }
        }

    }

    public static void loadDrinks(){
        productsMap.put("Drinks",loadCategory("Drinks"));

    }
    public static void loadFood(){
        productsMap.put("Food",loadCategory("Food"));
    }
    public static void loadDessert(){
        productsMap.put("Dessert",loadCategory("Dessert"));
    }
    public static void loadDProducts(){
        productsMap.put("D-Products",loadCategory("D-Products"));
    }

    public static ArrayList loadCategory(String category){

        final ArrayList categoryProducts = new ArrayList();
        Query query = productsRef.orderByChild("productType").equalTo(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    ProductDetails productDetails  =snap.getValue(ProductDetails.class);
                    final TableItem tableItem = new TableItem(productDetails.getName(),productDetails.getPrice(),productDetails.isHasTot());
                    tableItem.setQuantity(productDetails.getQuantity());
                    tableItem.setProductId(productDetails.getProductId());
                    tableItem.setCategory(productDetails.getCattegory());
                    tableItem.setAvailableQuantity(productDetails.getQuantity());
                    if(tableItem.isTotSales()){
                        final com.acesdatasystems.quickcash.model.TotProduct[] totProduct = new com.acesdatasystems.quickcash.model.TotProduct[1];
                        totProductsRef.child(tableItem.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                totProduct[0] = dataSnapshot.getValue(com.acesdatasystems.quickcash.model.TotProduct.class);

                                if (totProduct[0] !=null) {
                                    TotProduct temp = new TotProduct(totProduct[0].getName(), totProduct[0].getPrice(), totProduct[0].getTotLeft(), totProduct[0].getProductId(), totProduct[0].getTotsPerBottle());
                                    tableItem.getTotProductsList().add(temp);

                                }
                            }
                            //the value of the tot Product  has been saved in the first index of the array lis 'totProduct

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //todo handle error here
                            }
                        });
                    }
                    categoryProducts.add(tableItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo BarryHandle the error that occurs while loading product
            }

        });
            return categoryProducts;
    }

    public static boolean sendSaleData(Sale sale, List<SaleItem> saleItems, Context context){
        double amount = 0.0;
        if(saleItems.size() > 0){
            for (SaleItem t: saleItems) {
                amount += t.getQuantitySold() * t.getCost();
            }
        }
        try {
            BILL = new StringBuilder("\n"+LoginActivity.currentAccount.getBusinessName()+
                    "\n"+LoginActivity.currentAccount.getLocation()+
                    "\n"+LoginActivity.currentAccount.getMobileNumber()+
                    "\nTable #: " + "Counter Table" +
                    "\nWaiter : " + sale.getPersonnelName() +
                    DIVIDER +
                    String.format("%-20s%10s%10s","Product","Qty","Price" )+
                    DIVIDER);

            for(SaleItem item : saleItems){
                BILL.append(String.format("\n%-20s%10s%10s",item.getItemName(),item.getQuantitySold(), item.getCost()));
            }


            BILL.append(new StringBuilder(DIVIDER).append(
                    new StringBuilder("Order Total").append(new StringBuilder(
                            DIVIDER.replaceAll("-", " ").replace(
                                    DIVIDER.replaceAll("-", " ").substring(
                                            0, 12), "")).append(
                            new StringBuilder("GHS ").append(amount))).append(
                            new StringBuilder(DIVIDER)).append(
                            "Thank You. Hope You Enjoyed Our Service!\nReceipt Issued at : "+getCurrentTimeStamp()+"\n")));

            BILL.append(new StringBuilder(DIVIDER).append("Powered by QuickCashPOS " +
                    "Tel: 0302434633 / 0209044390\n\n\n\n"));

            mmOutputStream.write(BILL.toString().getBytes());


            // tell the user data were sent
            Toast.makeText(context,"Printing...",Toast.LENGTH_LONG);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean printOrder(String waiter,String tableName, ArrayList<TableItem> list, Context context){
        double amount = 0.0;
        if(list.size() > 0){
            for (TableItem t: list) {
                amount += t.getQuantity() * t.getPrice();
            }
        }
        try {
            BILL = new StringBuilder("\n"+LoginActivity.currentAccount.getBusinessName()+
                    "\n"+LoginActivity.currentAccount.getLocation()+
                    "\n"+"ORDER TICKET"+
                    "\nTable #: " + tableName +
                    "\nWaiter : " + LoginActivity.currentAccountUser.getName() +
                    DIVIDER +
                    String.format("%-20s%10s%10s","Product","Qty","Price" )+
                    DIVIDER);

            for(TableItem item : list){
                BILL.append(String.format("\n%-20s%10s%10s",item.getProductName(),item.getQuantity(), item.getPrice()));
            }


            BILL.append(new StringBuilder(DIVIDER).append(
                    new StringBuilder("Order Total").append(new StringBuilder(
                            DIVIDER.replaceAll("-", " ").replace(
                                    DIVIDER.replaceAll("-", " ").substring(
                                            0, 12), "")).append(
                            new StringBuilder("GHS ").append(amount).append(
                                    new StringBuilder("\n\n\n\n"))))));

            mmOutputStream.write(BILL.toString().getBytes());


            // tell the user data were sent
            Toast.makeText(context,"Printing...",Toast.LENGTH_LONG);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String getCurrentTimeStamp(){
//        Long tsLong = System.currentTimeMillis()/1000;
//        String ts = tsLong.toString();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
        String format = s.format(new Date());
        return format;
    }

    public boolean checkConnected(){

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected){
                    connectionState = true;
                }else {
                    connectionState = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return  connectionState;
    }

    private void checkNetStat() {
        try {
            checkConnected();
        } catch (Exception e) {
            Log.e("Error", "In onStartCommand");
            e.printStackTrace();
        }
        scheduleNext();
    }

    private void scheduleNext() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                checkNetStat(); }
        }, 2000);
    }

    public int onStartCommand() {
        mHandler = new android.os.Handler();
        checkNetStat();
        return START_STICKY;
    }

    public static void showNoNetDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.network_not_connected,null,false);
        builder.setView(viewInflated);
        builder.setCancelable(true);
        builder.show();

    }
}
