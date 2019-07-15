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

public class SalesSectionsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> salesFragmentList = new ArrayList<>();
    private final List<String> salesFragmentTitleList = new ArrayList<>();

    public void addFragment(Fragment fragment, String title){
        salesFragmentList.add(fragment);
        salesFragmentTitleList.add(title);
    }

    public SalesSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return salesFragmentTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return salesFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return salesFragmentList.size();
    }
}
