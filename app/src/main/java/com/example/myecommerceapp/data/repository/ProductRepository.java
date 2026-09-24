package com.example.myecommerceapp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;

import java.util.List;
import java.util.Map;

public interface ProductRepository {
    /** Live catalog, updated in real time when products change in the backend. */
    LiveData<Resource<List<Product>>> getProducts();

    /** Writes sample documents (id to fields) to the catalog. Debug builds only. */
    void seedProducts(Map<String, Map<String, Object>> products, ResultCallback<Void> callback);
}
