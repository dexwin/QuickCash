package com.acesdatasystems.quickcash.dataataptors;

/*
GENERAL IMPORTS
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acesdatasystems.quickcash.R;
import com.acesdatasystems.quickcash.model.ProductDetails;

import java.util.List;

public class StockProductAdapter extends RecyclerView.Adapter<StockProductCardHolder>{

    private List<ProductDetails> productList;
/*
constructor to initialize product list passed from datasource
*/
    public StockProductAdapter(List<ProductDetails> productList) {
        this.productList = productList;
    }

    @Override
    public StockProductCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_product_card, parent, false);

        return new StockProductCardHolder(itemView);
    }
/*
Binds view holder to datasource
*/

    @Override
    public void onBindViewHolder(final StockProductCardHolder holder, final int position) {
        final ProductDetails product = productList.get(position);
        holder.tvId.setText(product.getProductId());
        holder.tvProductName.setText(product.getName());
        holder.tvQuantity.setText(String.valueOf(product.getQuantity()));

        if(product.getCattegory().toLowerCase().contains("food")){
            holder.productIcon.setBackgroundResource(R.drawable.ic_meal_128x128);
            holder.tvQuantity.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }else if(product.getCattegory().toLowerCase().contains("drink")){
            holder.productIcon.setBackgroundResource(R.drawable.ic_drink_128x128);
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
        }else if(product.getCattegory().toLowerCase().contains("des")){
            holder.productIcon.setBackgroundResource(R.drawable.ic_desert_128x128);
            holder.tvQuantity.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }else if(product.getCattegory().toLowerCase().contains("d-product")){
            holder.productIcon.setBackgroundResource(R.drawable.ic_dproducts_128x128);
            holder.tvQuantity.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }
    }

/*
returns item count
*/
    @Override
    public int getItemCount() {
        return productList.size();
    }

}
