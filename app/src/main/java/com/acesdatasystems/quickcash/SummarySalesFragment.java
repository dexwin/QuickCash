package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SummarySalesFragment extends Fragment {

    private static final String TAG = "SummarySalesFragment";

    public SummarySalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary_sales, container, false);
    }



}
