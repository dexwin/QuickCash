package com.acesdatasystems.quickcash.model;

import android.support.annotation.NonNull;

import com.acesdatasystems.quickcash.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sheriff on 25-Sep-18.
 */

public class ProductDetails implements Comparable<ProductDetails>{
    String cattegory = "";
    String name ="";
    double cost =0;
    double price = 0.0;
    int quantity = 0;
    String productId = "";
    boolean hasTot = false;
    String productType = "";


    public ProductDetails() {
    }

    public ProductDetails(String cattegory, String name, double cost, double price, String productId,int availableQuantity, boolean hasTot) {
        this.cattegory = cattegory;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.productId = productId;
        this.hasTot = hasTot;
        productType = cattegory;
        this.quantity = availableQuantity;

    }

    public String getCattegory() {
        return cattegory;
    }

    public void setCattegory(String cattegory) {
        this.cattegory = cattegory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isHasTot() {
        return hasTot;
    }

    public void setHasTot(boolean hasTot) {
        this.hasTot = hasTot;
    }

    public static void uploadProduct(ProductDetails productDetails, final TotProduct totProduct){
       final String key = LoginActivity.accountsRef.child("products").child(productDetails.getCattegory()).push().getKey();
       productDetails.setProductId(key);
       totProduct.setProductId(key);
        LoginActivity.accountsRef.child("products").child(productDetails.getCattegory()).child(key).setValue(productDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (totProduct != null){
                    LoginActivity.accountsRef.child("totProducts").child(key).setValue(totProduct);
                }
            }
        });

    }

    //search product activity

    public static void searchProduct(String searchWord){
        Query query = LoginActivity.accountsRef.child("products").orderByChild("name").startAt(searchWord);
        final List<ProductDetails> productList = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    productList.add(snap.getValue(ProductDetails.class));
                }

                //todo place your codes for populating your the products list
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo BarryHandle the error that occurs while searching for the product
            }
        });
    }

    //load all products out of stock

    public static void loadOutOfStockProduct(int minmumQuantity){
        Query query = LoginActivity.accountsRef.child("products").orderByChild("quantity").endAt(minmumQuantity);
        final List<ProductDetails> productList = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    productList.add(snap.getValue(ProductDetails.class));
                }

                //todo place your codes for handling out of stock product
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo BarryHandle the error that occurs while searching for the product
            }
        });
    }

    /*
    @param this method load all product of the category
     */

    public static void loadProductByCategory(String category){
        Query query = LoginActivity.accountsRef.child("products").orderByChild("category").equalTo(category);
        final List<ProductDetails> productList = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    productList.add(snap.getValue(ProductDetails.class));
                }
                //todo place your codes for populating your the products list
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo BarryHandle the error that occurs while loadin product
            }
        });
    }

    public static void loadAllCategory(){
        final List<String> categoryList = new ArrayList<>();
        LoginActivity.accountsRef.child("category").orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    categoryList.add(snap.getValue(String.class));
                }
                //todo insert codes for handling category list
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo hadle error while loading category
            }
        });
    }

    public static void selectProductFromProductList(final ProductDetails product, final int quantity){
       Task<Void> task= LoginActivity.accountsRef.child("products").child(product.getProductId()).child("AvailableQuantity")
        .setValue(product.getQuantity()-quantity);
       task.addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               //todo add your codes for handling the completion

               double quanityCost = product.getPrice() * quantity;
           }
       });
       task.addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               //todo add your o failure handling codes here
           }
       });
    }

    public static void loadTotProductOfProduct(ProductDetails product){
        final TotProduct[] totProduct = new TotProduct[1];
        LoginActivity.accountsRef.child("product").child("totProducts").child(product.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totProduct[0] = dataSnapshot.getValue(TotProduct.class);
            }
            //the value of the tot Product  has been saved in the first index of the array lis 'totProduct
            //todo insert your codes for handling the totProduct here

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo handle error here
            }
        });
    }

    public static void addTotProductToTable(TotProduct totProduct, final ProductDetails product, int totQuantity){
        int productsLeft = product.quantity;
        int totsLeft = totProduct.getTotLeft() - totQuantity;
        final int finalTotLEft = totsLeft;
        if (totsLeft <=0){
            productsLeft -=1;
            totsLeft = totProduct.getTotsPerBottle() + totProduct.getTotLeft() - totQuantity;
        }
        final int finalProductLeft = productsLeft;

        Task<Void> totTask= LoginActivity.accountsRef.child("totProducts").child(totProduct.getProductId()).child("totLeft").setValue(totsLeft);

        // On success of the task
                totTask.addOnSuccessListener((new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //updating products' available quantity
                        if (finalTotLEft <=0){
                            LoginActivity.accountsRef.child("products").child(product.getProductId()).child("quantity")
                                    .setValue(finalProductLeft);
                        }
                        //todo place your codes here for handling success
                    }
                }));

                //on task failure event
        totTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //todo handle on add tot quantity failure
            }
        });

    }

    @Override
    public int compareTo(@NonNull ProductDetails productDetails) {
        return this.name.compareTo(productDetails.name);
    }
}
