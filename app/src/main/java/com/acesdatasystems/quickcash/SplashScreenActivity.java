package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class SplashScreenActivity extends AppCompatActivity {

    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        context = getApplicationContext();

            Thread timer = new Thread(){
                public void run(){
                    try{
                        sleep(1500);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }finally{

                            Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(loginActivity);
                            finish();
                    }
                }
            };
            timer.start();
    }

}
