package com.acesdatasystems.quickcash.dataataptors;

/*
GENERAL IMPORTS
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.acesdatasystems.quickcash.AdapterListeners.DeletePositionListener;
import com.acesdatasystems.quickcash.R;
import com.acesdatasystems.quickcash.datasource.TableItem;

import java.util.ArrayList;

public class TableItemEditAdapter extends ArrayAdapter<TableItem> {

    static DeletePositionListener deletePositionListener;

    /*
    constructor for constructing object and initializing application context and table items

     */
    public TableItemEditAdapter(Context context, ArrayList<TableItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final TableItem tableItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.table_item_edit_card, parent, false);
        }

        // Lookup view for data population
        TextView txtProductName = convertView.findViewById(R.id.txtProductName);
        final TextView txtProductQuantity = convertView.findViewById(R.id.txtProductQuantity);
        TextView txtProductPrice = convertView.findViewById(R.id.txtProductPrice);
        ImageButton add = convertView.findViewById(R.id.imgBtnAdd);
        ImageButton subtract = convertView.findViewById(R.id.imgBtnSubtract);
        ImageButton delete = convertView.findViewById(R.id.imgBtnDelete);

        // Populate the data into the template view using the data object
        txtProductName.setText(tableItem.getProductName());
        txtProductQuantity.setText(String.valueOf(tableItem.getQuantity()));
        txtProductPrice.setText(String.valueOf(tableItem.getPrice()));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = tableItem.getQuantity();
                qty ++;
                tableItem.setQuantity(qty);
                txtProductQuantity.setText(String.valueOf(qty));
            }
        });
        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = tableItem.getQuantity();
                if(qty > 1)
                qty --;
                tableItem.setQuantity(qty);
                txtProductQuantity.setText(String.valueOf(qty));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePositionListener.deleteTapped(position);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    // setter for delete with position listener
    public static void setDeletePositionListener(DeletePositionListener listener){
        deletePositionListener = listener;
    }
}

