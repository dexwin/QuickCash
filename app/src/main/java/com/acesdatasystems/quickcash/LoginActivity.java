package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.acesdatasystems.quickcash.model.AccountDetail;
import com.acesdatasystems.quickcash.model.AccountUser;
import com.acesdatasystems.quickcash.model.MyFirebaseInstanceIdService;
import com.acesdatasystems.quickcash.model.SubscriptionDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class LoginActivity extends AppCompatActivity {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText txtEmail;
    private EditText txtPin;
    private Button btnNext;
    public static AccountUser currentAccountUser = null;
    public static SubscriptionDetails subscriptionDetails= null;
    public static AccountDetail currentAccount = null;
    //firebase declarations
    public static FirebaseAuth mAuth;
    static FirebaseDatabase database;
   public static DatabaseReference usersRef;
   public static DatabaseReference accountsRef;
   public static StorageReference mStorageRef;
   public static DatabaseReference productsRef;
   public static DatabaseReference totProductsRef;
   public static DatabaseReference connectedRef;
   ProgressDialog dialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //firebase initializations

        if(database == null){
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
            connectedRef = database.getReference(".info/connected");
        }

        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        usersRef = database.getReference("AccountUser");
        accountsRef = database.getReference("Accounts");


        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtEmail);
        btnNext = findViewById(R.id.btnLogin);
        txtPin = findViewById(R.id.txtPin);

        // move password field from screen on startup
        txtPin.animate().translationXBy(1500f).setDuration(1);
        txtEmail.requestFocus(); // set focus on email field


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(!validateEmail(txtEmail.getText().toString().trim())){
                        Snackbar.make(view, "Enter a valid e-mail to proceed", Snackbar.LENGTH_LONG)
                                .setAction("CLOSE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                } else {

                    if (btnNext.getText().toString().equals("Next")) {

                        txtEmail.animate().translationXBy(-1500f).setDuration(500);

                        txtPin.animate().translationXBy(-1500f).setDuration(500);

                        txtPin.requestFocus();
                        btnNext.setText("Login");
                    } else {
                        if(txtPin.getText().length() < 6 || txtPin.getText().length() > 6){
                            Snackbar.make(view, "Enter your 6-digit pin code to Login", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();

                        }else {
                            //calling th e authenticator. I have replace your codes with firebase authentication codes
                            authenticate();
                        }

                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void authenticate(){
        dialog = ProgressDialog.show(LoginActivity.this, "",
                "Loading. Please wait...", true);
            mAuth.signInWithEmailAndPassword((txtEmail.getText().toString()), (txtPin.getText().toString()))
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!HomeActivity.hasApplicationStarted) {
                            if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Login", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    loginNextStep(user);

                                // TODO: 12/4/2018 set current user here

                                    dialog.dismiss();
                                }

                            } else {
                                dialog.dismiss();
                                Snackbar.make( btnNext, task.getException().getMessage(), Snackbar.LENGTH_LONG)
                                        .show();
                                // If sign in fails, display a message to the user.
                                Log.w("tLogin", "signInWithEmail:failure", task.getException());
                                txtEmail.animate().translationXBy(1500f).setDuration(500);
                                txtPin.animate().translationXBy(1500f).setDuration(500);

                                txtEmail.requestFocus();
                                btnNext.setText("Next");
                            }

                        }

                    });

        }


    /// what happens after firebase authentication with email has been successful
    private void loginNextStep(FirebaseUser user){
        //loading the user account info from firebase using the user credentials
         dialog = ProgressDialog.show(LoginActivity.this, "",
                "Checking Account Status, Please wait...", true);
            usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!HomeActivity.hasApplicationStarted) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        currentAccountUser = dataSnapshot.getValue(AccountUser.class);
                        // checking if the user has been blocked or has access to the firebase
                        if (currentAccountUser.getStatus().toLowerCase().contains("allowed")) {
                            //the user has not been blocked from accessing the database
                            productsRef = accountsRef.child(currentAccountUser.getRestaurant()).child("products");
                            totProductsRef = accountsRef.child(currentAccountUser.getRestaurant()).child("totProducts");
                            productsRef.keepSynced(true);
                            totProductsRef.keepSynced(true);
                            // checking the subscription status of the  business account
                            checkAccountSubscriptionStatus(currentAccountUser.getRestaurant());
                            dialog.dismiss();

                        } else {
                            //todo the user has been blocked from accessing the system and therefore the manager of the system has to be notified of the attempt
                            dialog.dismiss();
                            String blockMessage = String.format("%s tried to access the system at %s but has been blocked." +
                                            " If this is intended ignore this message else open the application and unblock him/her",
                                    currentAccountUser.getName(), new Date().toLocaleString());
                            notifyManagerOfAccountBlock( blockMessage);

                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                            alertDialog.setTitle("User Validation");
                            alertDialog.setMessage("Your Account has been blocked, Please Contact your Manager");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }
                }

                    @Override
                    public void onCancelled (DatabaseError error){
                        // Failed to read value
                        Log.w("database", "Failed to read value.", error.toException());

                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("Network Error");
                        alertDialog.setMessage("Please check Internet connection and try again");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();

                    }

            });

    }


    /**
     * @param accountToken
     *  creating a method to check the the status of the business account
     *  the subscription status could be subscribed or expired
     */
    private void checkAccountSubscriptionStatus(final String accountToken){


        final String subscriptionReference = String.format("%s/subscriptionDetail",accountToken);
        accountsRef.child(subscriptionReference).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                 subscriptionDetails = dataSnapshot.getValue(SubscriptionDetails.class);
                loadAccountDetails();
                //if statement to check whether it subscribed
                if (subscriptionDetails.getSubscriptionExpire().before(new Date())){
                    // subscriptions has expired
                    String blockMessage = String.format("%s tried to access the system at %s but your license has expired." +
                                    " Please call 03024344633 or any Aces Data Systems Manager re-subscription",
                            currentAccountUser.getName(),new Date().toLocaleString());
                    notifyManagerOfAccountBlock(blockMessage);

                    //todo barry create UX to notify the user of the expiration of the account
                }else{
                    if (currentAccountUser.getSetupStatus()){
                        //todo no profile setup  barry call the main activity from here
                        Intent profileSetupActivity = new Intent(getApplicationContext(), ProfileSetupActivity.class);
                        profileSetupActivity.putExtra("name", currentAccountUser.getName());
                        profileSetupActivity.putExtra("Source","Login");
                        startActivity(profileSetupActivity);
                        finish();
                    }else{
                        //the account has not expired
                        Intent home = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(home);
                        finish();
                        // Hurray now the user has successfully qualified to use the application
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("login", "Failed to read value.", error.toException());
                ////todo barry create a UX for notifying the user of the break in network connectivity
            }
        });
    }

    private  void loadAccountDetails(){

        accountsRef.child(currentAccountUser.getRestaurant()).child("accountInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentAccount = dataSnapshot.getValue(AccountDetail.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        try {
            String REG_TOKEN = FirebaseInstanceId.getInstance().getToken();
            Log.d("TOKEN", REG_TOKEN);
            MyFirebaseInstanceIdService.sendToken(REG_TOKEN);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void notifyManagerOfAccountBlock( String message){
//       final String token = accountsRef.child(currentAccountUser.getRestaurant()).child("users").push().getKey();
//       final Notification notification = new Notification(message,"AUTHENTICATION", token);
//
//        final ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                    database.getReference("Notification").child(dataSnapshot1.getKey()).child(token).setValue(notification);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        accountsRef.child(currentAccountUser.getRestaurant()).child("users").orderByChild("accountType").addListenerForSingleValueEvent(valueEventListener);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", message);
            jsonObject.put("title","LOGIN ATTEMPT");
            jsonObject.put("business", currentAccountUser.getRestaurant());
            jsonObject.put("level", "ADMINISTRATIVE");
            httpRequesst(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }



    public static boolean httpRequesst(JSONObject jsonParam){
        try{
            System.out.println(jsonParam);
            String query = jsonParam.toString().replace("\"{", "{").replace("}\"","}");
            query  = query.replace("\\", "").replace("\"[","[").replace("]\"","]");
            Log.i("JSON", query);
            HttpPost httpPost = new HttpPost( "http://us-central1-quick-cash-32ecf.cloudfunctions.net/notify");
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
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }
}