package com.example.myecommerceapp.fakes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myecommerceapp.data.model.Order;
import com.example.myecommerceapp.data.model.ShoppingCart;
import com.example.myecommerceapp.data.repository.OrderRepository;
import com.example.myecommerceapp.util.Resource;

import java.util.List;

/** Captures the checkout request so tests decide how the "server" responds. */
public class FakeOrderRepository implements OrderRepository {
    public ShoppingCart placedCart;
    public PlaceOrderCallback pendingCallback;
    public int placeOrderCalls = 0;

    @Override
    public void placeOrder(String uid, ShoppingCart cart, PlaceOrderCallback callback) {
        placeOrderCalls++;
        placedCart = cart;
        pendingCallback = callback;
    }

    @Override
    public LiveData<Resource<List<Order>>> observeOrders(String uid) {
        return new MutableLiveData<>(Resource.loading());
    }
}
