package com.example.myecommerceapp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myecommerceapp.data.model.Order;
import com.example.myecommerceapp.data.model.ShoppingCart;
import com.example.myecommerceapp.util.Resource;

import java.util.List;

public interface OrderRepository {

    interface PlaceOrderCallback {
        void onSuccess(String orderId);

        /** Stock ran out between viewing the cart and checking out. */
        void onOutOfStock(String productName);

        void onError(String message);
    }

    /**
     * Atomically checks stock, decrements it, writes the order and empties the cart.
     * Either all of these happen or none do.
     */
    void placeOrder(String uid, ShoppingCart cart, PlaceOrderCallback callback);

    /** Order history, newest first. */
    LiveData<Resource<List<Order>>> observeOrders(String uid);
}
