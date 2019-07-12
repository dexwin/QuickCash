package com.acesdatasystems.quickcash.dataataptors;

/*
GENERAL IMPORTS
 */
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acesdatasystems.quickcash.AdapterListeners.StockProductListener;
import com.acesdatasystems.quickcash.R;

public class StockProductCardHolder extends RecyclerView.ViewHolder {

    TextView tvProductName;
    TextView tvQuantity;
    TextView tvId;
    ImageView delete;
    ImageView edit;
    ImageView productIcon;
    private final String DELETE_COMMAND = "DELETE";
    private final String EDIT_COMMAND = "EDIT";

    // setup Listener for callback method
    public static StockProductListener listener;

    /*
    constructor initializes view item with its super constructor
    Sets onClick listener to view sub components
     */

    public StockProductCardHolder(View itemView) {
        super(itemView);
        tvId = itemView.findViewById(R.id.tvId);
        tvProductName = itemView.findViewById(R.id.tvStockProductName);
        tvQuantity = itemView.findViewById(R.id.tvStockProductQty);
        delete = itemView.findViewById(R.id.deleteImage);
        edit = itemView.findViewById(R.id.editImage);
        productIcon = itemView.findViewById(R.id.productIcon);




        // set delete onClick listener
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fire listener
                int position = getAdapterPosition();
                listener.stockProductClicked(DELETE_COMMAND,position);
            }
        });

        // set edit onClick listener
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fire listener
                int position = getAdapterPosition();
                listener.stockProductClicked(EDIT_COMMAND,position);
            }
        });
    }

    // create setter for listener

    public static void setListener(StockProductListener nListener) {
        listener = nListener;
    }
}
