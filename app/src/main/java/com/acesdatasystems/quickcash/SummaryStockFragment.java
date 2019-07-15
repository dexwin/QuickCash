package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acesdatasystems.quickcash.AdapterListeners.StockProductListener;
import com.acesdatasystems.quickcash.dataataptors.StockProductAdapter;
import com.acesdatasystems.quickcash.dataataptors.StockProductCardHolder;
import com.acesdatasystems.quickcash.model.ProductDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.acesdatasystems.quickcash.LoginActivity.productsRef;

public class SummaryStockFragment extends Fragment implements AdapterView.OnItemSelectedListener,StockProductListener{

    private static final String TAG = "SummaryStockFragment";
    RecyclerView stockItemsHolder;
    EditText etSearchStock;
    Spinner stockCategorySpinner;

    StockProductAdapter stockProductAdapter;
    List<ProductDetails> productArrayList;
    String selectedCat = "";
    public SummaryStockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_summary_stock, container, false);

         /*
        sets xml layout control to java controls
         */
         stockCategorySpinner = rootView.findViewById(R.id.stockCategorySpinner);
         etSearchStock = rootView.findViewById(R.id.etSearchStock);
         stockItemsHolder = rootView.findViewById(R.id.stockItemsHolder);

        stockCategorySpinner = rootView.findViewById(R.id.stockCategorySpinner);
        productArrayList = new ArrayList<>();
        stockProductAdapter = new StockProductAdapter(productArrayList);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.stock_search_category, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        stockCategorySpinner.setAdapter(adapter);
        stockCategorySpinner.setOnItemSelectedListener(this);


        loadStock();
        StockProductCardHolder.setListener(this);

        etSearchStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(etSearchStock.getText().length() < 1){
                    loadStock();
                }else if(selectedCat.equalsIgnoreCase("product")) {
                    searchStockProduct(etSearchStock.getText().toString().toLowerCase().trim());
                }else if(selectedCat.equalsIgnoreCase("category")) {
                    searchProductByCategory(etSearchStock.getText().toString().toLowerCase().trim());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCat =  adapterView.getItemAtPosition(i).toString();
        etSearchStock.setHint("Search by "+ selectedCat);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void loadStock(){

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productArrayList.clear();
                stockProductAdapter.notifyDataSetChanged();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ProductDetails productDetails = snapshot.getValue(ProductDetails.class);
                    productArrayList.add(productDetails);
                }
                if(productArrayList != null){
                    Collections.sort(productArrayList);
//            stockItemsHolder.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    stockItemsHolder.setLayoutManager(mLayoutManager);
                    stockItemsHolder.setItemAnimator(new DefaultItemAnimator());
                    stockItemsHolder.setAdapter(stockProductAdapter);

                    stockItemsHolder.setItemAnimator(new DefaultItemAnimator());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo barry handle error
            }
        });
    }

    @Override
    public void stockProductClicked(String command, final int position) {

        final ProductDetails selectedProduct = productArrayList.get(position);

        if(command.toLowerCase().equals("delete")){
            // delete product from firebase
            final String productName = selectedProduct.getName();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.delete_stock_confirmation_dialog,null,false);
            builder.setView(viewInflated);
            Button btnYes = viewInflated.findViewById(R.id.btnYes);
            Button btnNo = viewInflated.findViewById(R.id.btnNo);
            TextView tvProductName = viewInflated.findViewById(R.id.tvDeleteProductName);
            tvProductName.setText(productName + " ?");
            final AlertDialog alertDialog = builder.create();

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // delete product from firebase database
                    productsRef.child(selectedProduct.getProductId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            alertDialog.dismiss();
                        }
                    });
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
            alertDialog.setCancelable(true);
        }else {
            // edit product from firebase database

            final String productName = selectedProduct.getName();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.restock_input,null,false);
            builder.setView(viewInflated);
            Button btnYes = viewInflated.findViewById(R.id.btnYes);
            final EditText etNewStockQuantity = viewInflated.findViewById(R.id.etNewStockQuantity);
            TextView tvRestockProductName = viewInflated.findViewById(R.id.tvRestockProductName);
            TextView tvQuantityAvailable = viewInflated.findViewById(R.id.tvQuantityAvailable);
            tvQuantityAvailable.setText(String.valueOf(selectedProduct.getQuantity()));
            tvRestockProductName.setText(productName + " ?");
            final AlertDialog alertDialog = builder.create();

            btnYes.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(etNewStockQuantity.getText().length()>0){
                        final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                                "Submitting. Please wait...", true);
                        selectedProduct.setQuantity(selectedProduct.getQuantity() + Integer.parseInt(etNewStockQuantity.getText().toString().trim()));
                        productsRef.child(selectedProduct.getProductId()).setValue(selectedProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                alertDialog.dismiss();
                                dialog.dismiss();
                            }
                        });
                    }else {
                        Toast.makeText(getContext(),"Enter Valid Quantity", Toast.LENGTH_LONG).show();
                    }

                }
            });
            alertDialog.show();
            alertDialog.setCancelable(true);
        }
    }

    public void searchStockProduct(String searchWord){
            ArrayList<ProductDetails> products = new ArrayList<>();
            for (ProductDetails productDetails : productArrayList) {
                if (productDetails != null) {
                    if (productDetails.getName().toLowerCase().contains(searchWord)) {
                        products.add(productDetails);
                    }
                }
            }
            productArrayList.clear();
            productArrayList.addAll(products);
            stockProductAdapter.notifyDataSetChanged();
            if (productArrayList != null) {
                Collections.sort(productArrayList);
//            stockItemsHolder.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                stockItemsHolder.setLayoutManager(mLayoutManager);
                stockItemsHolder.setItemAnimator(new DefaultItemAnimator());
                stockItemsHolder.setAdapter(stockProductAdapter);
                stockItemsHolder.setItemAnimator(new DefaultItemAnimator());
            }
    }

    public void searchProductByCategory(String searchWord){
        ArrayList<ProductDetails> products = new ArrayList<>();
            for (ProductDetails productDetails : productArrayList) {
                if (productDetails != null) {
                    if (productDetails.getCattegory().toLowerCase().contains(searchWord)) {
                        products.add(productDetails);
                    }
            }
        }

        productArrayList.clear();
        productArrayList.addAll(products);
        stockProductAdapter.notifyDataSetChanged();
        if (productArrayList != null) {
            Collections.sort(productArrayList);
//            stockItemsHolder.addItemDecoration(new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL));
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            stockItemsHolder.setLayoutManager(mLayoutManager);
            stockItemsHolder.setItemAnimator(new DefaultItemAnimator());
            stockItemsHolder.setAdapter(stockProductAdapter);
            stockItemsHolder.setItemAnimator(new DefaultItemAnimator());
        }
    }
}
