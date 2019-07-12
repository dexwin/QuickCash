package com.acesdatasystems.quickcash;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.acesdatasystems.quickcash.AdapterListeners.UserListener;
import com.acesdatasystems.quickcash.model.AccountUser;
import com.acesdatasystems.quickcash.model.ShortAccountInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends BaseActivity implements UserListener{

    ImageView imgUsersBack;
    LinearLayout userHolderList;
    ArrayList<AccountUser> userArrayList;
    String accountType = "";
    ImageView imgAddUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AccountUser.setUserListener(this);

        // load all R fields
        imgUsersBack = findViewById(R.id.imgUsersBack);
        userHolderList = findViewById(R.id.userHolderList);
        imgAddUser = findViewById(R.id.imgAddUser);
        userArrayList = new ArrayList<>();



        imgAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 add new Account User
                AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
                View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_account_user,null,false);
                builder.setView(viewInflated);
                final AlertDialog alertDialog = builder.create();

                final EditText etName = viewInflated.findViewById(R.id.etName);
                final EditText etEmail = viewInflated.findViewById(R.id.etEmail);
                final EditText etPin = viewInflated.findViewById(R.id.etPin);
                final EditText etPhone = viewInflated.findViewById(R.id.etPhone);
                final Spinner spAccountType = viewInflated.findViewById(R.id.spAccountType);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                        R.array.account_type, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spAccountType.setAdapter(adapter);
                spAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        accountType = adapterView.getItemAtPosition(i).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                Button btnSubmit = viewInflated.findViewById(R.id.btnSubmit);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = etName.getText().toString();
                        final String email = etEmail.getText().toString();
                        final String pin = etPin.getText().toString();
                        final String phone = etPhone.getText().toString();

                        if (name.trim().isEmpty() || email.trim().isEmpty() || pin.trim().isEmpty() || phone.trim().isEmpty()){
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UsersActivity.this);
                            View viewInflated = LayoutInflater.from(UsersActivity.this).inflate(R.layout.fill_all_fields,null,false);
                            builder.setView(viewInflated);
                            builder.show();
                        } else if (email.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")){
                            Toast.makeText(getApplicationContext(),"Please Enter a valid Email Address",Toast.LENGTH_SHORT).show();
                        }else{
                            final ProgressDialog dialog = ProgressDialog.show(UsersActivity.this, "",
                                    "Creating User, Please wait...", true);
                           final ShortAccountInfo shortAccountInfo = new ShortAccountInfo(name,"",accountType, false,"Allowed");
                           final  AccountUser accountUser = new AccountUser(shortAccountInfo);
                           if(connectionState){
                               LoginActivity.mAuth.createUserWithEmailAndPassword(email,pin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                   @Override
                                   public void onComplete(@NonNull Task<AuthResult> task) {
                                       try {
                                           FirebaseUser firebaseUser = task.getResult().getUser();
                                           shortAccountInfo.setUserId(firebaseUser.getUid());
                                           accountUser.setRestaurant(LoginActivity.currentAccountUser.getRestaurant());
                                           accountUser.setUserId(shortAccountInfo.getUserId());
                                           accountUser.setEmail(email);
                                           accountUser.setSetupStatus(true);
                                           accountUser.setPassword(pin);
                                           accountUser.setPhoneNumber(phone);
                                           LoginActivity.accountsRef.getParent().child("AccountUser").child(accountUser.getUserId()).setValue(accountUser).addOnCompleteListener(new OnCompleteListener<Void>() {

                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   LoginActivity.accountsRef.child(accountUser.getRestaurant()).child("users").child(accountUser.getUserId()).setValue(shortAccountInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           Toast.makeText(getApplicationContext(), "User Created",Toast.LENGTH_SHORT).show();
                                                           loadAllUsers();
                                                           dialog.dismiss();
                                                           alertDialog.dismiss();
                                                       }

                                                   });
                                                   if(!connectionState){
                                                       if(dialog.isShowing()){
                                                           dialog.dismiss();
                                                           BaseActivity.showNoNetDialog(UsersActivity.this);
                                                       }
                                                   }
                                               }

                                           });
                                       }catch (Exception e){

                                       }
                                   }
                               });
                           }else {
                                   if(dialog.isShowing()){
                                       dialog.dismiss();
                                       BaseActivity.showNoNetDialog(UsersActivity.this);

                               }
                           }
                        }
                    }
                });

                alertDialog.show();
                alertDialog.setCancelable(true);
            }
        });

        //onCreate methods
        imgUsersBackOnAction();
        loadAllUsers();
    }

    public void imgUsersBackOnAction(){
        imgUsersBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void loadAllUsers(){

//        final AccountUser user = AccountUser.getRootView(getApplicationContext(),"lord barry",true,false);
//        userArrayList.add(user);


        System.out.println("some I'm about to load some bitch in here");
        if((userHolderList).getChildCount() > 0)
            (userHolderList).removeAllViews();

        userArrayList.removeAll(userArrayList);
//        final List<ShortAccountInfo> allUsers = new ArrayList<>();
        LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    AccountUser accountUser = new AccountUser(snapshot.getValue(ShortAccountInfo.class));
                    userArrayList.add(new AccountUser(getApplicationContext(),accountUser));
                }

                //todo insert your codes for handling the usersInfo
                (userHolderList).removeAllViews();
                for(AccountUser u : userArrayList){
                    if (u!=null) {
                        if(u.getRootView().getParent() != null)
                            ((ViewGroup)u.getRootView().getParent()).removeView(u.getRootView());
                        userHolderList.addView(u.getRootView());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo handle the error here
            }
        });



    }

    @Override
    public void tableTapped(View selectedUser) {
        if(userHolderList.getChildCount() != 0){
            for (int i = 0; i < userHolderList.getChildCount(); i++) {
                ((AccountUser)userHolderList.getChildAt(i).getTag()).getUserLayoutSelected().setVisibility(View.GONE);
            }
        }
        ((AccountUser)selectedUser.getTag()).getUserLayoutSelected().setVisibility(View.VISIBLE);
    }



}
