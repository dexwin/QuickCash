package com.acesdatasystems.quickcash;

/*
GENERAL IMPORTS
 */
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CategoryBottonSheet extends BottomSheetDialogFragment {

    /*
    Inflates layout into view using layout inflater
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_sheet,container,false);
        return view;
    }
}
