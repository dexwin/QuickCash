package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acesdatasystems.quickcash.AdapterListeners.ProductItemListener;
import com.acesdatasystems.quickcash.dataataptors.ProductListAdapter;
import com.acesdatasystems.quickcash.dataataptors.TableItemAdapter;
import com.acesdatasystems.quickcash.datasource.Table;
import com.acesdatasystems.quickcash.datasource.TableItem;
import com.acesdatasystems.quickcash.model.KitchenProduct;
import com.acesdatasystems.quickcash.model.RestHeader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.acesdatasystems.quickcash.HomeActivity.saveList;

public class ProductsActivity extends BaseActivity implements View.OnClickListener, ProductItemListener{

    static List<TableItem> tableArrayList = new ArrayList<>();
    private TableItemAdapter tableItemAdapter = null;
    public static String KITCHEN_LOCATION = "kitchen";

    ListView lvProductList;
    static List<TableItem> productList =new ArrayList<>();
    ListView productListView;
    ProductListAdapter productAdapter = null;
    ImageView imgBtnServe,imgQuickCheckout,imgClearList;
    TextView txtSearch;
    static ImageView imgPrintConnect;
    int quantity = 1;
    BottomNavigationView category_bottom_navigation;
    Map<String, Integer> mapIndex;
    String selectedOption = "";
    TableItem selectedTableProduct = null;
    ArrayList<TableItem> searchList = new ArrayList();
    int pos = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mapIndex = new LinkedHashMap<String, Integer>();
//        productList = new ArrayList<>();
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productListView = findViewById(R.id.productsListView);
        txtSearch = findViewById(R.id.txtSearch);
        imgBtnServe = findViewById(R.id.imgBtnServe);
        category_bottom_navigation = findViewById(R.id.category_bottom_navigation);
        category_bottom_navigation.setSelectedItemId(R.id.action_drinks);
        imgClearList = findViewById(R.id.imgClearList);
        imgQuickCheckout = findViewById(R.id.imgQuickCheckout);
        imgPrintConnect = findViewById(R.id.imgPrintConnect);

        isProductActivityStarted = true;

        if(HomeActivity.isPrinterConnected){
            imgPrintConnect.setBackgroundResource(R.drawable.printer_wireless_connect_48x48);
        }else {
            imgPrintConnect.setBackgroundResource(R.drawable.printer_wireless_disconnect_48x48);
        }

        if(!HomeActivity.selectedTable.getTableName().equalsIgnoreCase("Quick Table")){
            imgClearList.setVisibility(View.GONE);
            imgQuickCheckout.setVisibility(View.GONE);
        }else if (HomeActivity.selectedTable.getTableName().equalsIgnoreCase("Quick Table")){
            imgBtnServe.setVisibility(View.GONE);
        }else  {
            imgClearList.setVisibility(View.VISIBLE);
            imgQuickCheckout.setVisibility(View.VISIBLE);
        }

        tableItemAdapter = new TableItemAdapter(this,(ArrayList<TableItem>) tableArrayList);
        productListView.setAdapter(tableItemAdapter);



        productAdapter = new ProductListAdapter(this,(ArrayList<TableItem>) productList);
        productAdapter.setProductItemListener(this);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                category_bottom_navigation.setSelectedItemId(R.id.action_search);
                productList.clear();
                productAdapter.notifyDataSetChanged();
                searchProduct(txtSearch.getText().toString().toLowerCase().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // sets home table items to product table items and checks if table does not contain default item
        if(HomeActivity.selectedTable.getTableItemList().size()>0){
            if(HomeActivity.selectedTable.getTableItemList().get(0).getProductName().equalsIgnoreCase("Long Press To Serve")){
                HomeActivity.selectedTable.getTableItemList().clear();
                tableArrayList.clear();
                tableItemAdapter.notifyDataSetChanged();
            }
        }

        if(HomeActivity.selectedTable.isServing()){
            tableArrayList.clear();
            tableArrayList.addAll(HomeActivity.selectedTable.getTableItemList());
            tableItemAdapter.notifyDataSetChanged();
        }



        lvProductList = findViewById(R.id.list_products);
        lvProductList.setAdapter(productAdapter);

        String [] sideIndexList = getResources().getStringArray(R.array.product_index_array);

        getIndexList(Arrays.asList(sideIndexList));
        displayIndex();
        imgBtnServeOnAction();
        if (productList.isEmpty()) {
            loadCategoryDrinks();
        }{
            Collections.sort(productList);
            productAdapter.notifyDataSetChanged();
        }
        setupBottomSheetNavigationForProductCategory();
        imgClearListOnAction();
        imgCheckoutOnAction();
        loadCategoryDrinks();

    }
    public static void printerConnected(){
        if(isProductActivityStarted){
            isPrinterConnected = true;
            imgPrintConnect.setBackgroundResource(R.drawable.printer_wireless_connect_48x48);
        }

    }
    public static void printerDisconnected(){
        if(isProductActivityStarted){
            isPrinterConnected = false;
            imgPrintConnect.setBackgroundResource(R.drawable.printer_wireless_disconnect_48x48);
        }

    }
    private void getIndexList(List<String> productList) {
        for(int i = 0; i<productList.size(); i++){
            String product = productList.get(i);
            String index = product.substring(0,1);
            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex() {
        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            indexLayout.addView(textView);
        }

    }

    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        lvProductList.setSelection(mapIndex.get(selectedIndex.getText()));
    }
    @Override
    public void productTapped(final TableItem item) {
            quantity = 1;
            selectedTableProduct = new TableItem();
            selectedTableProduct.setProductName(item.getProductName());
            selectedTableProduct.setProductId(item.getProductId());
            selectedTableProduct.setPrice(item.getPrice());
            selectedTableProduct.setCategory(item.getCategory());
            selectedTableProduct.setTotSales(item.isTotSales());
            selectedTableProduct.setTotProductsList(item.getTotProductsList());
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductsActivity.this);

//                builder.setTitle("");
            View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.product_item_click_fragment, null, false);
            final EditText input = viewInflated.findViewById(R.id.txtQty);
            final TextView name = viewInflated.findViewById(R.id.txtProduct);
            final TextView price = viewInflated.findViewById(R.id.txtPrice);
            final ImageView up = viewInflated.findViewById(R.id.imgBtnUp);
            final ImageView down = viewInflated.findViewById(R.id.imgBtnDown);

            final ConstraintLayout dynamic_products_layout = viewInflated.findViewById(R.id.dynamic_product_layout);
            final EditText etDynamicPrice = viewInflated.findViewById(R.id.etDynamicPrice);
            final Button btnAddProduct = viewInflated.findViewById(R.id.btnAddProduct);

            if(selectedTableProduct.getCategory().toLowerCase().contains("d-products")){
                price.setText("0.0");
                selectedTableProduct.setPrice(0.0);
                dynamic_products_layout.setVisibility(View.VISIBLE);
                etDynamicPrice.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() != 0){
                            price.setText(etDynamicPrice.getText().toString());
                            selectedTableProduct.setPrice(Double.valueOf(etDynamicPrice.getText().toString()));
                        }

                    }
                });
            }else {
                dynamic_products_layout.setVisibility(View.GONE);
            }

            if (selectedTableProduct.isTotSales()) {
                //loading tot product
                selectedTableProduct.setTotServing(false);

                final ListView lvServingsList = viewInflated.findViewById(R.id.lvServingsList);
                final ArrayList<TableItem> totServingsList = new ArrayList();
                totServingsList.add(selectedTableProduct);

                for(TableItem tableItem : selectedTableProduct.getTotProductsList()){
                    totServingsList.add(tableItem);
                }
                final ArrayAdapter<TableItem> totListAdapter = new ArrayAdapter<TableItem>(lvServingsList.getContext(), android.R.layout.simple_list_item_1, totServingsList) {

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);

                            text.setTextColor(Color.BLACK);

                        return view;
                    }
                };
                lvServingsList.setAdapter(totListAdapter);
                lvServingsList.setVisibility(View.VISIBLE);

                lvServingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedTableProduct = new TableItem();
                        selectedTableProduct.setProductId(((TableItem)lvServingsList.getItemAtPosition(i)).getProductId());
                        selectedTableProduct.setProductName(((TableItem)lvServingsList.getItemAtPosition(i)).getProductName());
                        selectedTableProduct.setPrice(((TableItem)lvServingsList.getItemAtPosition(i)).getPrice());
                        selectedTableProduct.setCategory(((TableItem)lvServingsList.getItemAtPosition(i)).getCategory());
                        selectedTableProduct.setTotServing(false);

                        if(i > 0){
                            quantity = 1;
                            input.setText(String.valueOf(quantity));
                            if(!selectedTableProduct.isTapped()){
                                selectedTableProduct.setProductName("(" + item.getProductName() + ")  " + selectedTableProduct.getProductName());
                                name.setText(selectedTableProduct.getProductName());
                                selectedTableProduct.setTapped(true);
                            }else {
                                selectedTableProduct.setProductName(selectedTableProduct.getProductName());
                                name.setText(selectedTableProduct.getProductName());
                            }
                            selectedTableProduct.setTotSales(true);

                        }else {
                            selectedTableProduct.setProductName(item.getProductName());
                            name.setText(selectedTableProduct.getProductName());

                        }
                        price.setText(String.valueOf(selectedTableProduct.getPrice()));

                    }
                });
            }


            input.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() != 0) {
                        quantity = Integer.valueOf(input.getText().toString());
                        input.setSelection(input.getText().length());
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0)
                        quantity = Integer.valueOf(input.getText().toString());
                    input.setSelection(input.getText().length());
                }
            });
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quantity++;
                    input.setText(String.valueOf(quantity));
                    input.setSelection(input.getText().length());
                }
            });
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (quantity > 1) {
                        quantity--;
                        input.setText(String.valueOf(quantity));
                        input.setSelection(input.getText().length());
                    }
                }
            });

        final TableItem oldTableItem = findTableItem(selectedTableProduct);
        if (oldTableItem != null) {
            pos = tableArrayList.indexOf(oldTableItem);
            if(oldTableItem.getQuantity() > 1){
                quantity = oldTableItem.getQuantity();
                input.setText(String.valueOf(quantity));
            }
        }


        name.setText(selectedTableProduct.getProductName());
            price.setText(String.valueOf(selectedTableProduct.getPrice()));
            builder.setView(viewInflated);

// Set up the buttons
            final AlertDialog alertDialog = builder.create();
            btnAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedTableProduct.getPrice() == 0){
                        Toast.makeText(getApplicationContext(),"Set Price", Toast.LENGTH_SHORT).show();
                    }else {
                        if(oldTableItem != null && selectedTableProduct.getProductName().equals(oldTableItem.getProductName()) && !oldTableItem.getCategory().toLowerCase().equals("d-products")){
                            selectedTableProduct.setQuantity(quantity);
                            tableArrayList.remove(pos);
                            tableArrayList.add(pos,selectedTableProduct);
                            tableItemAdapter.notifyDataSetChanged();
                        }else {
                            selectedTableProduct.setQuantity(quantity);
                            tableArrayList.add(selectedTableProduct);
                            tableItemAdapter.notifyDataSetChanged();
                        }
                        alertDialog.dismiss();
                    }
                }
            });
            alertDialog.show();
    }

    private TableItem findTableItem(TableItem item){
        for (TableItem tableItem : tableArrayList){
            if(tableItem.getProductName().equals(item.getProductName())){
                return tableItem;
            }
        }
        return null;
    }
    public void imgBtnServeOnAction(){
        imgBtnServe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sets home table items to product table items and checks if table does not contain default item
                double total = 0.0;
                TableItem tableItem = null;
                for (TableItem t : tableArrayList){
                    total += t.getPrice()*t.getQuantity();
                }
                Table refreshedTable = Table.getRootView(HomeActivity.selectedTable.getTableName(),total,true,getApplicationContext(),HomeActivity.tableCardHolder);

                refreshedTable.getTableItemList().addAll(tableArrayList);
                HomeActivity.refreshTable(refreshedTable);
                //calling the method to upload table ietms
                uploadKitchenTableItems();
                finish();
            }
        });
    }

    public void setupBottomSheetNavigationForProductCategory(){
        category_bottom_navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_food:
                                item.setChecked(true);
                                loadCategoryFood();
                                break;
                            case R.id.action_drinks:
                                item.setChecked(true);
                                loadCategoryDrinks();

                                break;
                            case R.id.action_desert:
                                item.setChecked(true);
                                loadCategoryDesert();
                                break;
                            case R.id.action_dynamic_product:
                                item.setChecked(true);
                                loadCategoryDynamic();
                                break;
                            case R.id.action_search:
                                productAdapter.notifyDataSetChanged();
                                productList.clear();
                                item.setChecked(true);
                                break;
                        }
                        return false;
                    }
                });

    }

    public void loadCategoryDrinks(){
        productList.clear();
        if(!productsMap.isEmpty()) {
            productList.addAll(productsMap.get("Drinks"));
            Collections.sort(productList);
        }
        productAdapter.notifyDataSetChanged();

    }
    public void loadCategoryFood() {
        productAdapter.notifyDataSetChanged();
        productList.clear();
        if(!productsMap.isEmpty()) {
            productList.addAll(productsMap.get("Food"));
            Collections.sort(productList);
        }
        productAdapter.notifyDataSetChanged();
    }
    public void loadCategoryDesert(){
        productAdapter.notifyDataSetChanged();
        productList.clear();
        if(!productsMap.isEmpty()) {
            productList.addAll(productsMap.get("Dessert"));
            Collections.sort(productList);
        }
        productAdapter.notifyDataSetChanged();
    }
    public void loadCategoryDynamic(){
        productAdapter.notifyDataSetChanged();
        productList.clear();
        if(!productsMap.isEmpty()) {
            productList.addAll(productsMap.get("D-Products"));
            Collections.sort(productList);
        }
        productAdapter.notifyDataSetChanged();
    }

    /*
        searching product form firebase
     */
    public void searchProduct(String name){
//        productList.clear();
//        productAdapter.notifyDataSetChanged();
//        Query query = productsRef.orderByChild("name").startAt(name);
//        final List<ProductDetails> productLists = new ArrayList<>();
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snap : dataSnapshot.getChildren()){
//                    ProductDetails productDetails  =snap.getValue(ProductDetails.class);
////                    productLists.add(productDetails);
//                    TableItem tableItem = new TableItem(productDetails.getName(),productDetails.getPrice(),productDetails.isHasTot());
//                    tableItem.setQuantity(productDetails.getQuantity());
//                    tableItem.setProductId(productDetails.getProductId());
//                    tableItem.setCategory(productDetails.getCattegory());
//                    tableItem.setAvailableQuantity(productDetails.getQuantity());
//                    productList.add(tableItem);
//                }
//                Collections.sort(productList);
//                productAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //todo BarryHandle the error that occurs while loading product
//            }
//        });

        List<TableItem> products = new ArrayList<>();
        for (ArrayList list : productsMap.values()){
            for (Object productDetails : list){
                if (productDetails != null){
                    if (((TableItem) productDetails).getProductName().toLowerCase().contains(name)){
                        products.add((TableItem) productDetails);
                    }
                }
            }
        }
        Collections.sort(products);
        productList.addAll(products);
        productAdapter.notifyDataSetChanged();
    }


    public void imgCheckoutOnAction(){
        imgQuickCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPrinterConnected){
                        processPayment();
                }else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProductsActivity.this);
                    View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.device_not_connected,null,false);
                    builder.setView(viewInflated);

                    builder.show();
                    builder.setCancelable(true);
                }
            }
        });
    }
    public void imgClearListOnAction(){
        imgClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tableArrayList.size()> 0){
                    for (TableItem i: tableArrayList){
                        i.setProductAdded(false);
                    }
                    tableArrayList.clear();
                    tableItemAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    public void processPayment(){
        selectedOption = "cash";
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProductsActivity.this);
        View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.payment_options,null,false);
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

        final android.app.AlertDialog localDialog = builder.create();
        builder.setCancelable(true);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectedOption){
                    case "cash":
                        // print cash receipt
                        if(cbIssueSaleReceipt.isChecked()){
                            try {
                                if(HomeActivity.sendData(LoginActivity.currentAccountUser,tableArrayList,ProductsActivity.this)){
                                    for (TableItem i: tableArrayList){
                                        i.setProductAdded(false);
                                    }
                                    tableArrayList.clear();
                                    tableItemAdapter.notifyDataSetChanged();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Sales Recorded", Toast.LENGTH_LONG).show();
                            double total = 0.0;
                            for(TableItem i: tableArrayList){
                                total += i.getPrice() * i.getQuantity();
                            }
                            saveList(new RestHeader().createHeader(tableArrayList,LoginActivity.currentAccountUser,0.00,total));
                            // TODO: 1/23/2019 Dompreh save sales to firebase
                        }

                        break;
                    case "card":
                        // print card receipt

                        Toast.makeText(ProductsActivity.this,"Upgrade ",Toast.LENGTH_SHORT).show();
                        break;
                    case "momo":
                        // print momo receipt

                        Toast.makeText(ProductsActivity.this,"Upgrade ",Toast.LENGTH_SHORT).show();
                        break;
                }
                localDialog.dismiss();
            }
        });

        localDialog.show();
    }

    private void uploadKitchenTableItems(){
       final String token = LoginActivity.productsRef.push().getKey();
       ArrayList<TableItem> tableItems = new ArrayList<>();
       for (TableItem tableItem : tableArrayList){
           tableItem.setTotProductsList(null);
           tableItems.add(tableItem);
       }
        KitchenProduct kitchenProduct = new KitchenProduct(HomeActivity.selectedTable.getTableName(),LoginActivity.currentAccountUser.getName(),tableItems,false);
        kitchenProduct.setProductReference(token);

        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child(KITCHEN_LOCATION).child(token).setValue(kitchenProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                KitchenProduct.orderTableKitchenIdList.put(HomeActivity.selectedTable.getTableName(),token);
                addListenerToLocation(token);
            }
        });
    }

    private void addListenerToLocation(final String token){
        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child(KITCHEN_LOCATION).orderByKey().equalTo(token)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //todo Barry handle when the kitchen complete the product
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        KitchenProduct kitchenProduct = dataSnapshot1.getValue(KitchenProduct.class);
                        if (kitchenProduct.isComplted()) {
                            showNotification(kitchenProduct.getNotifcationTagLine(kitchenProduct),kitchenProduct.getTableNumber());
                            //the below codes removes the products immediately after the the notification has been created
//                            dataSnapshot.getRef().removeValue();
                        }
                    }

                    this.finalize();
                }catch (Exception e){
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public  void showNotification(String product,String tableNumber) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("NOTIFICATION", "From: " + product);


        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("QC KITCHEN");
        notificationBuilder.setContentText(String.format("%s is ready to be served at table %s", product, tableNumber));
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setSmallIcon(R.drawable.qclogo_small);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }

//todo kitchen items this is the methos to get the kitchen products
    public static void kitchenItemsListener(){
        final List<KitchenProduct> loadedTableItems = new ArrayList<>();
        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child(KITCHEN_LOCATION).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    loadedTableItems.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        KitchenProduct kitchenProduct = snapshot.getValue(KitchenProduct.class);
                        if (!kitchenProduct.isComplted()){
                            //todo barry handle adding kitchen product to your UI component here
                        }

                    }

                    //

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void completeCooking(KitchenProduct kitchenProduct){
        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child(KITCHEN_LOCATION).child(kitchenProduct.getProductReference()).child("completed").setValue(true)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //todo barry remove kitchen product from your kitchen products list
                String dog;
            }
        });
    }






}
