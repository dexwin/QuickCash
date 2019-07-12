package com.acesdatasystems.quickcash.dataataptors;

/*
  GENERAL IMPORTS FOR SALEITEMADAPTER
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acesdatasystems.quickcash.R;
import com.acesdatasystems.quickcash.model.Sale;

import java.util.List;

public class SaleItemAdapter extends RecyclerView.Adapter<SaleItemHolder>{

    private List<Sale> saleList;

    /*
    constructor to initialize product list passed from datasource
     */
    public SaleItemAdapter(List<Sale> productList) {
        this.saleList = productList;
    }

    /*
    onCreateViewHolder method loads layout into view
     */
    @Override
    public SaleItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // load layout into view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sale_item_card, parent, false);

        return new SaleItemHolder(itemView);
    }

    /*
    Binds view holder to datasource
     */
    @Override
    public void onBindViewHolder(final SaleItemHolder holder, final int position) {
        final Sale sale = saleList.get(position);
        holder.tvSaleId.setText(sale.getSaleId());
        holder.tvWaiter.setText(sale.getPersonnelName());
        holder.tvDate.setText(sale.getTimeSoldASString());
        holder.tvTotal.setText("GHS "+String.valueOf(sale.getTotCost()));

    }

    /*
    returns item count
     */

    @Override
    public int getItemCount() {
        return saleList.size();
    }

}
