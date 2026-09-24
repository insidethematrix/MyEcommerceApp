package com.example.myecommerceapp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;

import java.util.Map;

public interface CartRepository {
    /** Live map of productId to quantity for the user's cart, in insertion order. */
    LiveData<Resource<Map<String, Integer>>> observeCart(String uid);

    void addToCart(String uid, String productId, ResultCallback<Void> callback);

    /** Sets the quantity; zero or less removes the line. */
    void setQuantity(String uid, String productId, int quantity);

    void clearCart(String uid);
}
