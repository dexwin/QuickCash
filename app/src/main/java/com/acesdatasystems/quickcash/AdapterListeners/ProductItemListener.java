package com.acesdatasystems.quickcash.AdapterListeners;

import com.acesdatasystems.quickcash.datasource.TableItem;

// Interface to setup a custom event trigger listener for product item onClick

public interface ProductItemListener {
     void productTapped(TableItem tableItem);
}
