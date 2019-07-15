package com.acesdatasystems.quickcash;
/*
GENERAL IMPORTS
 */
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StockSectionsPagerAdapter extends FragmentPagerAdapter{

    private final List<Fragment> stockFragmentList = new ArrayList<>();
    private final List<String> stockFragmentTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        stockFragmentList.add(fragment);
        stockFragmentTitleList.add(title);
    }

    public StockSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stockFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return stockFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return stockFragmentList.size();
    }
}
