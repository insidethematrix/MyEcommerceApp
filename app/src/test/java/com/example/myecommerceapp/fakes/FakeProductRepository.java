package com.example.myecommerceapp.fakes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.data.repository.ProductRepository;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;

import java.util.List;
import java.util.Map;

public class FakeProductRepository implements ProductRepository {
    public final MutableLiveData<Resource<List<Product>>> products = new MutableLiveData<>(Resource.loading());
    public Map<String, Map<String, Object>> seeded;

    public void emit(List<Product> list) {
        products.setValue(Resource.success(list));
    }

    @Override
    public LiveData<Resource<List<Product>>> getProducts() {
        return products;
    }

    @Override
    public void seedProducts(Map<String, Map<String, Object>> seed, ResultCallback<Void> callback) {
        seeded = seed;
        callback.onSuccess(null);
    }
}
