package com.acesdatasystems.quickcash.model;

import android.util.Log;

import com.acesdatasystems.quickcash.LoginActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Sheriff on 11/19/2018.
 */


public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String REG_TOKEN = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN", REG_TOKEN);
        sendToken(REG_TOKEN);
    }

    public static void  sendToken(String token){
        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant())
                .child("users").child(LoginActivity.currentAccountUser.getUserId()).child("reg_token").setValue(token);
    }

}
