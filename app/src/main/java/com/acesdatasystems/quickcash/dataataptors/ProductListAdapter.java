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

import com.acesdatasystems.quickcash.AdapterListeners.ProductItemListener;
import com.acesdatasystems.quickcash.R;
import com.acesdatasystems.quickcash.datasource.TableItem;

import java.util.ArrayList;

public class ProductListAdapter extends ArrayAdapter<TableItem> {

     ProductItemListener productItemListener;
/*
 constructor to initialize table items by calling its super constructor
 */
    public ProductListAdapter(Context context, ArrayList<TableItem> items) {
        super(context, 0, items);
    }
/*
 Populate the data into the template view using the data object
 */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final TableItem tableItem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
        }
        // Lookup view for data population
        TextView txtProductName = convertView.findViewById(R.id.txtProductN);
        TextView txtProductPrice = convertView.findViewById(R.id.txtProductP);
        // check if item is a dynamic product
        if(tableItem.getCategory().toLowerCase().contains("d-products")){
            txtProductPrice.setVisibility(View.GONE);
        }else {
            txtProductPrice.setVisibility(View.VISIBLE);
        }
        // Populate the data into the template view using the data object
        txtProductName.setText(tableItem.getProductName());
        txtProductPrice.setText(String.valueOf(tableItem.getPrice()));

        convertView.setClickable(true);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productItemListener.productTapped(tableItem);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    /*
    setter for callback function listener
     */
    public void setProductItemListener(ProductItemListener listener){
        this.productItemListener =listener;
    }
}
