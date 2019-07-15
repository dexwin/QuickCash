package com.acesdatasystems.quickcash.model;

/*
GENERAL IMPORTS
 */
import com.acesdatasystems.quickcash.LoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sheriff on 04-Oct-18.
 */

public class ShortAccountInfo {

    /*
    variable declarations
     */
    String name;
    String userId;
    String accountType;
    boolean online;
    String status;
    String reg_token;

    /*
    Default Constructor
     */
    public ShortAccountInfo() {
    }

    /*
    Constructor to instantiate and initialize ShortAccountInfo object
     */
    public ShortAccountInfo(String name, String userId, String aacountType, boolean online, String statis) {
        this.name = name;
        this.userId = userId;
        this.accountType = aacountType;
        this.online = online;
        this.status = statis;
    }

    /*
    Setters and Getters
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReg_token() {
        return reg_token;
    }

    public void setReg_token(String reg_token) {
        this.reg_token = reg_token;
    }

    /*
        this method queries all the users of the business from the firebase
         */
    public static void loadAllAccountShortUsers(){
        final List<ShortAccountInfo> allUsers = new ArrayList<>();
        LoginActivity.accountsRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    allUsers.add(snapshot.getValue(ShortAccountInfo.class));
                }

                //todo insert your codes for handling the usersInfo
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo handle the error here
            }
        });
    }

    public static void blockUser(final ShortAccountInfo user, final String status){
        // updating users at users
       Task<Void> blockedTask = LoginActivity.accountsRef.child("users").child(user.userId).child("status").setValue(status);
        blockedTask.addOnSuccessListener(new OnSuccessListener<Void>() {
           //updating users info at users
           @Override
           public void onSuccess(Void aVoid) {
                LoginActivity.accountsRef.getRoot().child("AccountUsers").child(user.userId).child("status").setValue(status);
           }
       });
    }


}
