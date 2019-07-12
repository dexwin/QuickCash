package com.acesdatasystems.quickcash.AdapterListeners;

import com.acesdatasystems.quickcash.model.Sale;

// Interface to setup a custom event trigger listener for sale item onClick

public interface SaleItemListener {

    void saleItemClicked(Sale item);
}
