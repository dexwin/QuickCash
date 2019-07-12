package com.acesdatasystems.quickcash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileSetupActivity extends AppCompatActivity {
    private ImageView imgBack,
                      imgMenu,
                      imgVisible,
                      imgEditUsername,
                      imgEditPassword,
                      imgProfileImage;

    private TextView txtUserEmail;
    private EditText txtNewPin;
    private EditText txtUsername;
    private boolean isPinVisible = false;
    private Button btnContinue,
    btnUpdateProfile;

    private Uri profileImage;
    ProgressDialog dialog;
    String intentSource = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        imgBack = findViewById(R.id.imgBack);
        imgMenu = findViewById(R.id.imgMenu);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtNewPin = findViewById(R.id.txtNewPin);
        imgVisible = findViewById(R.id.imgVisible);
        txtUsername = findViewById(R.id.txtUsername);
        imgEditPassword = findViewById(R.id.imgEditPassword);
        imgEditUsername = findViewById(R.id.imgEditUsername);
        imgProfileImage = findViewById(R.id.imgProfileImage);
        btnContinue = findViewById(R.id.btnContinue);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        intentSource = getIntent().getStringExtra("Source");

        Bitmap image = LoginActivity.currentAccountUser.getProfilePic();
        if(image != null){
            imgProfileImage.setImageBitmap(image);
        }
        if(intentSource.equals("Home")){
            btnContinue.setVisibility(View.INVISIBLE);
        }
        if(intentSource.equals("Home")){
            btnUpdateProfile.setVisibility(View.VISIBLE);
        }
        String name = this.getIntent().getStringExtra("name");
        txtUserEmail.setText(LoginActivity.currentAccountUser.getEmail());
        txtNewPin.setText(LoginActivity.currentAccountUser.getPassword());
        txtUsername.setText(LoginActivity.currentAccountUser.getName());
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(intentSource.equals("Home")){
                    onBackPressed();
                }else{
                    System.exit(0);
                }
            }
        });

        imgVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPinVisible){
                    txtNewPin.setTransformationMethod(new PasswordTransformationMethod());
                    isPinVisible = false;
                }else {
                    txtNewPin.setTransformationMethod(null);
                    isPinVisible = true;
                }

            }
        });
        imgEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUsername.setEnabled(true);
            }
        });
        imgEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNewPin.setEnabled(true);
            }
        });
        imgProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // continue to home
                saveProfile();
            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save profile
                saveProfile();
            }
        });
    }
    private  String filePath="";
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            filePath = data.getData().getPath();
            profileImage = Uri.fromFile(new File(data.getData().getPath()));

            switch (requestCode) {

                case 1:
                    decodeUri(data.getData());
                    break;
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception in onActivityResult : " + e.getMessage());
        }
    }
    public  void decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            imgProfileImage.setImageBitmap(bitmap);
            LoginActivity.currentAccountUser.setProfilePic(bitmap);

        } catch (FileNotFoundException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
    }

    public void saveProfile(){
        // Get the data from an ImageView as bytes
        imgProfileImage.setDrawingCacheEnabled(true);
        imgProfileImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgProfileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String imageRef = String.format("userProfiles/%s.jpeg", LoginActivity.currentAccountUser.getUserId());
        UploadTask uploadTask = LoginActivity.mStorageRef.child(imageRef).putBytes(data);
        dialog = ProgressDialog.show(ProfileSetupActivity.this, "",
                "Updating Profile" +" Please wait...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(15000);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //todo barry Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(),"Update Failed, Please check your network connection", Toast.LENGTH_LONG).show();
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                updateInformation(taskSnapshot.getDownloadUrl().toString());
            }
        });
//        String imageRef = String.format("userProfiles/%s.jpeg",LoginActivity.currentAccountUser.getUserId());
//        LoginActivity.mStorageRef.child(imageRef).putFile(Uri.fromFile(new File(filePath))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                updateInformation();
//            }
//        });
    }

    public void updateInformation(final String downloadpath){
        LoginActivity.mAuth.getCurrentUser().updatePassword(txtNewPin.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LoginActivity.usersRef.child(LoginActivity.currentAccountUser.getUserId()).child("password").setValue(txtNewPin.getText().toString().trim());
                LoginActivity.usersRef.child(LoginActivity.currentAccountUser.getUserId()).child("name").setValue(txtUsername.getText().toString().trim());
                LoginActivity.usersRef.child(LoginActivity.currentAccountUser.getUserId()).child("imgUrl").setValue(downloadpath);
                LoginActivity.usersRef.child(LoginActivity.currentAccountUser.getUserId()).child("setupStatus").setValue(false);
                LoginActivity.accountsRef.child(LoginActivity.currentAccountUser.getRestaurant()).child("users").child(LoginActivity.currentAccountUser.getUserId()).child("name")
                        .setValue(txtUsername.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Update Successful", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
//                        if(intentSource.equals("Home")){
//                           // do nothing
//                        }else{
//                            Intent home = new Intent(getApplicationContext(),HomeActivity.class);
//                            LoginActivity.usersRef.child(LoginActivity.currentAccountUser.getUserId()).child("setupStatus").setValue(false);
//                            startActivity(home);
//                        }
                    }
                });

            }
        });

    }




}
