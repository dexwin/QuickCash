package com.acesdatasystems.quickcash.dataataptors;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acesdatasystems.quickcash.AdapterListeners.SaleItemListener;
import com.acesdatasystems.quickcash.R;
import com.acesdatasystems.quickcash.model.Sale;

public class SaleItemHolder extends RecyclerView.ViewHolder {

    TextView tvSaleId;
    TextView tvWaiter;
    TextView tvDate;
    TextView tvTotal;
    ImageView imgViewList;

    /*
    setup Listener for callback method
     */
    public static SaleItemListener listener;

    /*
    constructor to initialize the view item using its super constructor
    Sets view onclick listener at construction phase
     */

    public SaleItemHolder(View itemView) {
        super(itemView);
        tvSaleId = itemView.findViewById(R.id.tvSaleId);
        tvWaiter = itemView.findViewById(R.id.tvSoldBy);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvTotal = itemView.findViewById(R.id.tvTotal);
        imgViewList = itemView.findViewById(R.id.imgViewList);



        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fire listener
                Sale item = new Sale();
                item.setPersonnelName(tvWaiter.getText().toString());
                item.setTotCost(Double.valueOf(tvTotal.getText().toString().substring(4)));
                item.setSaleId(tvSaleId.getText().toString());
                listener.saleItemClicked(item);
            }
        });
    }

    /*
    create setter for listener
     */

    public static void setListener(SaleItemListener nListener) {
        listener = nListener;
    }
}
