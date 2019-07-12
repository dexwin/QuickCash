package com.acesdatasystems.quickcash;
/*
GENERAL IMPORTS
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailStockFragment extends Fragment {

    private static final String TAG = "DetailStockFragment";

    public DetailStockFragment() {
        // Required empty public constructor
    }

    /*
        Inflates layout into view using layout inflater
         */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_stock, container, false);
    }



}
