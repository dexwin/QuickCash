package com.acesdatasystems.quickcash.model;

/*
GENERAL IMPORTS
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.acesdatasystems.quickcash.AdapterListeners.UserListener;
import com.acesdatasystems.quickcash.LoginActivity;
import com.acesdatasystems.quickcash.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AccountUser extends  ShortAccountInfo{

    /*
    Initialization of variables
     */

    private String name = "";
    private String email = "";
    private String userId = "";
    private String password = "";
    private String status = "";
    private String restaurant = "";
    private String imgUrl = "";
    private String phoneNumber = "";
    private String accountType = "";
    private boolean setupStatus = false;
    private boolean online = false;
    private Bitmap profilePic = null;

    private Switch swBlockUser = null;
    private LinearLayout userLayoutSelected;
    private View rootView;
    static int positionCount = 1;
    public static UserListener userListener;

    public static AccountUser user;

    private static List<AccountUser> accountUserList = new ArrayList<>();

    /*
    Constructor to create and  initialize AccountUser object
     */
    public AccountUser(String name, String email, String userId, String password, String status, String restaurant, String imgUrl, String phoneNumber, String accountType, boolean setupStatus, boolean online) {

        super( name,  userId,  accountType,  online,  status);
        this.email = email;
        this.userId = userId;
        this.password = password;
        this.status = status;
        this.restaurant = restaurant;
        this.imgUrl = imgUrl;
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
        this.setupStatus = setupStatus;
        this.online = online;
    }
    /*
    Overloaded Constructor
     */
    public AccountUser(){}
    public AccountUser(ShortAccountInfo shortAccountInfo){
        super(shortAccountInfo.name,shortAccountInfo.getUserId(),shortAccountInfo.accountType,shortAccountInfo.online,shortAccountInfo.getStatus());
        this.name = shortAccountInfo.name;
        this.userId = shortAccountInfo.getUserId();
        this.accountType = shortAccountInfo.accountType;
        this.online = shortAccountInfo.online;
        this.status = shortAccountInfo.status;

    }
    /*
    Overloaded Constructor
     */

    public AccountUser (Context context, String userFullName, boolean isOnline, boolean isBlockUser){
        this.name = userFullName;

        rootView = LayoutInflater.from(context).inflate(R.layout.user_account,null, false);
        TextView txtUserFullName = rootView.findViewById(R.id.txt_full_username);
        TextView txtUserStatus = rootView.findViewById(R.id.txtUserStatus);
        swBlockUser = rootView.findViewById(R.id.swBlockState);
        userLayoutSelected = rootView.findViewById(R.id.user_layout_selected);

        txtUserFullName.setText(userFullName);

        swBlockUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    // TODO: 12/3/2018 Block User
                    // switch enabled
                }else {
                    // switch disabled
                }
            }
        });
        /*
        Check user availability ie. Online / Offline
         */
        if(isOnline){
            swBlockUser.setChecked(true);
            txtUserStatus.setText("ONLINE");
            txtUserStatus.setTextColor(Color.parseColor("#2bc063"));
        }else if(isBlockUser){
            swBlockUser.setChecked(false);
            txtUserStatus.setText("BLOCKED");
            txtUserStatus.setTextColor(Color.parseColor("#858585"));
        }else if(!isBlockUser && !isBlockUser ){
            swBlockUser.setChecked(true);
            txtUserStatus.setText("OFFLINE");
            txtUserStatus.setTextColor(Color.parseColor("#c02b2b"));
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fire click event from here
                userListener.tableTapped(rootView);
            }
        });
        positionCount ++;
    }
    /*
    Overloaded Constructor
     */
    public AccountUser (Context context, final AccountUser user){

        super(user.name,  user.userId,  user.accountType, user.online,  user.status);
        this.email = user.email;
        this.userId = user.userId;
        this.password = user.password;
        this.status = user.status;
        this.restaurant = user.restaurant;
        this.imgUrl = user.imgUrl;
        this.phoneNumber = user.phoneNumber;
        this.accountType = user.accountType;
        this.setupStatus = user.setupStatus;
        this.online = user.online;
        this.name = user.name;

        boolean isBlockUser = false;
        if (user.status.toLowerCase().contains("blocked")) isBlockUser = true;



        rootView = LayoutInflater.from(context).inflate(R.layout.user_account,null, false);
        TextView txtUserFullName = rootView.findViewById(R.id.txt_full_username);
        TextView txtUserStatus = rootView.findViewById(R.id.txtUserStatus);
        swBlockUser = rootView.findViewById(R.id.swBlockState);
        userLayoutSelected = rootView.findViewById(R.id.user_layout_selected);

        txtUserFullName.setText(name);
        swBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getStatus().trim().equalsIgnoreCase("Blocked")){
                    System.out.println("sdffffffffffffffffffffffffffffffffffffffffffffffffffff       I am very mad right now");
                    LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child("users").child(user.getUserId()).child("status").setValue("Allowed")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    LoginActivity.usersRef.child(user.getUserId()).child("status").setValue("Allowed");
                                }
                            });
                }else{
                    LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child("users").child(user.getUserId()).child("status").setValue("Blocked")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    LoginActivity.usersRef.child(user.getUserId()).child("status").setValue("Blocked");
                                }
                            });
                }
            }
        });

        swBlockUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            }
        });

        if(online){
            swBlockUser.setChecked(true);
            txtUserStatus.setText("ONLINE");
            txtUserStatus.setTextColor(Color.parseColor("#2bc063"));
        }else if(isBlockUser){
            swBlockUser.setChecked(false);
            txtUserStatus.setText("BLOCKED");
            txtUserStatus.setTextColor(Color.parseColor("#858585"));
        }else if(!isBlockUser && !isBlockUser ){
            swBlockUser.setChecked(true);
            txtUserStatus.setText("OFFLINE");
            txtUserStatus.setTextColor(Color.parseColor("#c02b2b"));
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fire click event from here
                userListener.tableTapped(rootView);
            }
        });
        this.getRootView().setTag(this);
        positionCount ++;
    }
    /*
    Getters and Setters
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean getSetupStatus() {
        return setupStatus;
    }

    public void setSetupStatus(boolean setupStatus) {
        this.setupStatus = setupStatus;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public static List<AccountUser> getAccountUserList(){
        return accountUserList;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public View getRootView() {
        return rootView;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public LinearLayout getUserLayoutSelected() {
        return userLayoutSelected;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public Switch getSwBlockUser() {
        return swBlockUser;
    }

    public static AccountUser getRootView(final Context c, String username, boolean isUserLogin, boolean isBlockUser) {
        user = new AccountUser(c,username,isUserLogin,isBlockUser);
        user.getRootView().setTag(user);
        return user;
    }
    public static AccountUser getRootView(final Context c,AccountUser accountUser) {
        user = accountUser;
        user.setRootView(LayoutInflater.from(c).inflate(R.layout.user_account,null, false));
        user.getRootView().setTag(user);
        return user;
    }

    public static void setUserListener(UserListener listener) {
        userListener = listener;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
