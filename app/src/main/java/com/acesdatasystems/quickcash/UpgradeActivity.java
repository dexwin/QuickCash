package com.acesdatasystems.quickcash;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

public class UpgradeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        FloatingActionButton upgradeBasic = findViewById(R.id.fabUpgradeBasic);
        FloatingActionButton upgradePro = findViewById(R.id.upgradePro);

        upgradeBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpgradeActivity.this);
                View viewInflated = LayoutInflater.from(UpgradeActivity.this).inflate(R.layout.agreement_layout,null,false);
                builder.setView(viewInflated);
                builder.show();
            }
        });

        upgradePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpgradeActivity.this);
                View viewInflated = LayoutInflater.from(UpgradeActivity.this).inflate(R.layout.agreement_layout,null,false);
                builder.setView(viewInflated);
                builder.show();
            }
        });
    }
}
