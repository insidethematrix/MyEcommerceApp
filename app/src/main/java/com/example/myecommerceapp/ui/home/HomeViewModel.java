package com.example.myecommerceapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.data.repository.ProductRepository;
import com.example.myecommerceapp.util.Event;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final LiveData<Resource<List<Product>>> allProducts;
    private final MediatorLiveData<Resource<List<Product>>> visibleProducts = new MediatorLiveData<>();
    private final MutableLiveData<Event<Resource<Void>>> seedResult = new MutableLiveData<>();

    private String query = "";
    private String category = null;

    public HomeViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.allProducts = productRepository.getProducts();
        visibleProducts.addSource(allProducts, unused -> refresh());
    }

    /** The catalog after search and category filters are applied. */
    public LiveData<Resource<List<Product>>> getProducts() {
        return visibleProducts;
    }

    public LiveData<Event<Resource<Void>>> getSeedResult() {
        return seedResult;
    }

    public void setQuery(String query) {
        if (Objects.equals(this.query, query)) return;
        this.query = query;
        refresh();
    }

    /** @param category a category key, or null for all */
    public void setCategory(String category) {
        if (Objects.equals(this.category, category)) return;
        this.category = category;
        refresh();
    }

    public boolean hasActiveFilter() {
        return (query != null && !query.trim().isEmpty()) || category != null;
    }

    public void seed(Map<String, Map<String, Object>> products) {
        productRepository.seedProducts(products, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                seedResult.setValue(new Event<>(Resource.success(null)));
            }

            @Override
            public void onError(String message) {
                seedResult.setValue(new Event<>(Resource.error(message)));
            }
        });
    }

    private void refresh() {
        Resource<List<Product>> source = allProducts.getValue();
        if (source == null) return;
        if (source.getStatus() == Resource.Status.SUCCESS) {
            visibleProducts.setValue(Resource.success(
                    ProductFilter.apply(source.getData(), query, category)));
        } else {
            visibleProducts.setValue(source);
        }
    }
}
