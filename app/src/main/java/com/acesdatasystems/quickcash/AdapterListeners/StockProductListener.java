package com.acesdatasystems.quickcash.AdapterListeners;

// Interface to setup a custom event trigger listener for stock item onClick

public interface StockProductListener {
    void stockProductClicked(String command, int position);
}
