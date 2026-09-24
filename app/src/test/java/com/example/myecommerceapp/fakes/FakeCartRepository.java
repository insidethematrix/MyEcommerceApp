package com.example.myecommerceapp.fakes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myecommerceapp.data.repository.CartRepository;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;

import java.util.LinkedHashMap;
import java.util.Map;

/** In-memory cart that behaves like the Firestore one: every write re-emits the live map. */
public class FakeCartRepository implements CartRepository {
    public final MutableLiveData<Resource<Map<String, Integer>>> cart = new MutableLiveData<>(Resource.loading());
    private final Map<String, Integer> quantities = new LinkedHashMap<>();
    public int writeCount = 0;
    public boolean cleared = false;

    public void emit(Map<String, Integer> initial) {
        quantities.clear();
        quantities.putAll(initial);
        cart.setValue(Resource.success(new LinkedHashMap<>(quantities)));
    }

    public Integer quantityOf(String productId) {
        return quantities.get(productId);
    }

    @Override
    public LiveData<Resource<Map<String, Integer>>> observeCart(String uid) {
        return cart;
    }

    @Override
    public void addToCart(String uid, String productId, ResultCallback<Void> callback) {
        Integer current = quantities.get(productId);
        setQuantity(uid, productId, current == null ? 1 : current + 1);
        callback.onSuccess(null);
    }

    @Override
    public void setQuantity(String uid, String productId, int quantity) {
        writeCount++;
        if (quantity <= 0) {
            quantities.remove(productId);
        } else {
            quantities.put(productId, quantity);
        }
        cart.setValue(Resource.success(new LinkedHashMap<>(quantities)));
    }

    @Override
    public void clearCart(String uid) {
        cleared = true;
        quantities.clear();
        cart.setValue(Resource.success(new LinkedHashMap<>(quantities)));
    }
}
