package com.acesdatasystems.quickcash.dataataptors;

/*
GENERAL IMPORTS
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acesdatasystems.quickcash.R;
import com.acesdatasystems.quickcash.model.SaleItem;

import java.util.ArrayList;

public class SaleItemComponentsAdapter extends ArrayAdapter<SaleItem> {

    /*
    constructor to initialize saleItems using its super constructor
     */
    public SaleItemComponentsAdapter(Context context, ArrayList<SaleItem> saleItems) {
        super(context, 0, saleItems);
    }

    /*
    Populate the data into the template view using the data object
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SaleItem saleItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.table_item_card, parent, false);
        }
        // Lookup view for data population
        TextView txtProductName = convertView.findViewById(R.id.txtProductName);
        TextView txtProductQuantity = convertView.findViewById(R.id.txtProductQuantity);
        TextView txtProductPrice = convertView.findViewById(R.id.txtProductPrice);
        // Populate the data into the template view using the data object
        txtProductName.setText(saleItem.getItemName());
        txtProductQuantity.setText(String.valueOf(saleItem.getQuantitySold()));
        txtProductPrice.setText(String.valueOf(saleItem.getCost()));

        // Return the completed view to render on screen
        return convertView;
    }
}
