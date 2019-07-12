package com.acesdatasystems.quickcash;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.acesdatasystems.quickcash.model.ProductDetails;
import com.acesdatasystems.quickcash.model.TotProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.acesdatasystems.quickcash.LoginActivity.productsRef;
import static com.acesdatasystems.quickcash.LoginActivity.totProductsRef;

public class AddProduct extends Fragment{

    private static final String TAG = "AddProductFragment";

    private Switch swPartSalesAvailable;
    private LinearLayout partSalesLinearLayout;
    ArrayAdapter categoryAdapter;
    ArrayList<String> categoryList = new ArrayList<>();

    private AutoCompleteTextView tvCategory;
    private TextInputLayout product_quantity_text_input_layout, product_price_text_input_layout;

    private EditText etProductName,
            etProductQuantity,
            etProductPrice,
            etPartSalesProductName,
            etPartSalesPrice,
            etQuantityPerBottle;
    private Button btnAddProduct;
    private Spinner spProductCategory;
    private String productCategory = "";

    Handler mHandler;

    public AddProduct() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_product, container, false);
        swPartSalesAvailable = rootView.findViewById(R.id.swPartSalesAvailable);
        partSalesLinearLayout = rootView.findViewById(R.id.partSalesLinearLayout);
        etProductName = rootView.findViewById(R.id.etProductName);
        etProductQuantity = rootView.findViewById(R.id.etProductQuantity);
        etProductPrice = rootView.findViewById(R.id.etProductPrice);
        etPartSalesProductName = rootView.findViewById(R.id.etPartSalesProductName);
        etPartSalesPrice = rootView.findViewById(R.id.etPartSalesPrice);
        etQuantityPerBottle = rootView.findViewById(R.id.etQuantityPerBottle);
        btnAddProduct = rootView.findViewById(R.id.btnAddProduct);
        spProductCategory = rootView.findViewById(R.id.spProductCategory);
        product_quantity_text_input_layout = rootView.findViewById(R.id.product_quantity_text_input_layout);
        product_price_text_input_layout = rootView.findViewById(R.id.product_price_text_input_layout);


        productsRef.addValueEventListener(new ProductsChildEventListener());

        totProductsRef.addValueEventListener(new TotProductsChildEventListener());


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_list, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spProductCategory.setAdapter(adapter);
        spProductCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                productCategory = adapterView.getItemAtPosition(i).toString();
                if (!productCategory.toLowerCase().contains("drinks")){
                    partSalesLinearLayout.setVisibility(View.GONE);
                    swPartSalesAvailable.setVisibility(View.GONE);
                }else {
                    if(swPartSalesAvailable.isChecked()){
                        partSalesLinearLayout.setVisibility(View.VISIBLE);
                    }
                    swPartSalesAvailable.setVisibility(View.VISIBLE);
                }

                 if(productCategory.toLowerCase().contains("d-products")){
                    product_quantity_text_input_layout.setVisibility(View.GONE);
                    product_price_text_input_layout.setVisibility(View.GONE);
                }else {
                     product_quantity_text_input_layout.setVisibility(View.VISIBLE);
                     product_price_text_input_layout.setVisibility(View.VISIBLE);
                 }

                 if(productCategory.toLowerCase().contains("food")){
                    etProductQuantity.setText("0");
                    product_quantity_text_input_layout.setVisibility(View.GONE);
                 }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setupPartSalesSwitch();
        btnAddProductOnAction();
        return rootView;
    }

    public void setupPartSalesSwitch(){
        swPartSalesAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (productCategory.toLowerCase().contains("drink")) {
                    if (isChecked) {
                        partSalesLinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        partSalesLinearLayout.setVisibility(View.GONE);
                    }
                }else{
                    Toast.makeText(getContext(),"Selected Category has no Servings",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void btnAddProductOnAction(){
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String productName = "";
                Double productPrice=0.0;
                Integer productQuantity =0;
                boolean hasTotProduct= false;
                boolean allFieldsHasBeenField = true;
                 TotProduct totProduct = null;

                if(productCategory.toLowerCase().contains("d-products")){
                    etProductQuantity.setText("0");
                    etProductPrice.setText("0.00");
                }

                if(etProductName.getText().length() > 0){
                    productName = etProductName.getText().toString();
                }else{
                    allFieldsHasBeenField = false;
                }

                if(etProductQuantity.getText().length() > 0){
                     productQuantity = Integer.parseInt(etProductQuantity.getText().toString());
                }else{
                    allFieldsHasBeenField = false;
                }
                if(etProductPrice.getText().length() > 0){
                    productPrice = Double.parseDouble(etProductPrice.getText().toString());
                }else{
                    allFieldsHasBeenField = false;
                }
//                if(productCategory.toLowerCase().contains("drink")) productQuantity = 999999999;


                if(swPartSalesAvailable.isChecked()){
                    hasTotProduct = true;
                    String partSalesProductName ="";
                    Double partSalesPrice=0.0;
                    Integer servingsPerBottle=0;

                    if(etPartSalesProductName.getText().length() > 0){
                        partSalesProductName = etPartSalesProductName.getText().toString();
                    }else{
                        allFieldsHasBeenField = false;
                    }

                    if(etPartSalesPrice.getText().length() > 0){
                         partSalesPrice = Double.parseDouble(etPartSalesPrice.getText().toString());
                    }else{
                        allFieldsHasBeenField = false;
                    }
                    if(etQuantityPerBottle.getText().length() > 0){
                         servingsPerBottle = Integer.parseInt(etQuantityPerBottle.getText().toString());
                    }else{
                        allFieldsHasBeenField = false;
                    }

                    totProduct =new TotProduct(partSalesProductName,partSalesPrice,0,"",servingsPerBottle);

                }

                // 1.create product   2. add product to database
               if (allFieldsHasBeenField){
                   // Set this up in the UI thread.
                   final ProgressDialog dialog = ProgressDialog.show(getContext(), "",
                           "Submitting. Please wait...", true);

                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               Thread.sleep(5000);
                               if (dialog.isShowing()) {
                                   dialog.dismiss();
//                                   AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                                   alertDialog.setTitle("Alert");
//                                   alertDialog.setMessage("Alert message to be shown");
//                                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                                           new DialogInterface.OnClickListener() {
//                                               public void onClick(DialogInterface dialog, int which) {
//                                                   dialog.dismiss();
//                                               }
//                                           });
//                                   alertDialog.show();
                               }
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }
                   }).start();
                   String productId = LoginActivity.accountsRef.child("Accounts").getKey();
                   //adding product to database
                   ProductDetails productDetails = new ProductDetails(productCategory,productName,0.0,productPrice,productId,productQuantity,hasTotProduct );
                   final String key = productsRef.child(productDetails.getCattegory()).push().getKey();
                   productDetails.setProductId(key);
                   if(hasTotProduct) totProduct.setProductId(key);
                   final TotProduct totProducts = totProduct;

                   productsRef.child(key).setValue(productDetails, new DatabaseReference.CompletionListener() {
                       @Override
                       public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                           if(swPartSalesAvailable.isChecked()){
                               if (totProducts != null){
                                   totProducts.setProductId(key);
                                   totProductsRef.child(key).setValue(totProducts).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           // TODO: 11/10/2018 notify data send to firebase
                                       }
                                   });
                               }
                           }
                           new Thread(new Runnable() {
                               @Override
                               public void run() {
                                   try {
                                       Thread.sleep(1500);
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

               }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                   View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.fill_all_fields,null,false);
                   builder.setView(viewInflated);
                   builder.show();
               }

            }
        });
    }
    public void setNewTotProduct(TotProduct oldProduct, TotProduct newProduct){
        oldProduct = newProduct;
    }

    void workerThread(){
        // And this is how you call it from the worker thread:
        Message message = mHandler.obtainMessage(1,"Product Save to local memory");
        message.sendToTarget();
    }

    class AddProductTask extends AsyncTask<Object,Integer, Integer>{

        @Override
        protected Integer doInBackground(Object... objects) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    class ProductsChildEventListener implements ValueEventListener{

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            ProductDetails productDetails = dataSnapshot.getValue(ProductDetails.class);

            switch (productDetails.getCattegory().toLowerCase()){
                case "food":
                    BaseActivity.loadFood();
                    break;

                case "drinks":
                    BaseActivity.loadDrinks();
                    break;
                case "dessert":
                    BaseActivity.loadDessert();
                    break;
                case "d-products":
                    BaseActivity.loadDProducts();
                    break;
            }
            etPartSalesProductName.setText("");
            etPartSalesPrice.setText("");
            etQuantityPerBottle.setText("");
            etProductName.setText("");
            etProductQuantity.setText("");
            etProductPrice.setText("");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
    class TotProductsChildEventListener implements ValueEventListener{

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            swPartSalesAvailable.setChecked(false);
            etPartSalesProductName.setText("");
            etPartSalesPrice.setText("");
            etQuantityPerBottle.setText("");

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
