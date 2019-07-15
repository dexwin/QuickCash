package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class StockActivity extends BaseActivity {

    private ImageView imgBack;
    private StockSectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager stockViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        imgBack = findViewById(R.id.imgStockBack);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stockViewPager = findViewById(R.id.stockContainer);
        setupViewPager(stockViewPager);

        TabLayout tabLayout = findViewById(R.id.stockActivityTabs);
        tabLayout.setupWithViewPager(stockViewPager);

        imgBackOnAction();

    }

    public void imgBackOnAction(){
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StockActivity.super.onBackPressed();
            }
        });
    }


    public void setupViewPager(ViewPager viewPager){
         StockSectionsPagerAdapter adapter = new StockSectionsPagerAdapter(getSupportFragmentManager());
         adapter.addFragment(new AddProduct(),"ADD STOCK");
         adapter.addFragment(new SummaryStockFragment(),"SUMMARY");
//         adapter.addFragment(new DetailStockFragment(),"DETAIL");
         viewPager.setAdapter(adapter);
    }

}
