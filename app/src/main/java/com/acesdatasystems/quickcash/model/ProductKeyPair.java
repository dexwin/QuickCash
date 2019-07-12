package com.acesdatasystems.quickcash.model;


import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Sheriff on 11/8/2018.
 */

public class ProductKeyPair extends HashMap<String,String> {
    private HashMap<String, String> reversehas;
    public ProductKeyPair() {
        reversehas = new HashMap<>();
    }



    @Override
    public String put(String key, String value) {
        reversehas.put(value,key);
        return super.put(key, value);
    }

    @Override
    public String get(Object key) {
        return super.get(key);
    }
    public String getKey(String vcalue){
        return reversehas.get(values());
    }
}
