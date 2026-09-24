package com.example.myecommerceapp.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myecommerceapp.R;
import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.data.repository.AuthRepository;
import com.example.myecommerceapp.data.repository.CartRepository;
import com.example.myecommerceapp.data.repository.ProductRepository;
import com.example.myecommerceapp.util.Event;
import com.example.myecommerceapp.util.Resource;
import com.example.myecommerceapp.util.ResultCallback;

import java.util.List;
import java.util.Map;

public class ProductDetailViewModel extends ViewModel {
    private final CartRepository cartRepository;
    private final String uid;
    private final LiveData<Resource<List<Product>>> products;
    private final LiveData<Resource<Map<String, Integer>>> cart;

    private final MediatorLiveData<Product> product = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> quantityInCart = new MediatorLiveData<>();
    private final MutableLiveData<Event<Resource<Void>>> addResult = new MutableLiveData<>();
    private String productId;

    public ProductDetailViewModel(ProductRepository productRepository, CartRepository cartRepository,
                                  AuthRepository authRepository) {
        this.cartRepository = cartRepository;
        this.uid = authRepository.getCurrentUserId();
        this.products = productRepository.getProducts();
        this.cart = cartRepository.observeCart(uid);
        product.addSource(products, unused -> updateProduct());
        quantityInCart.addSource(cart, unused -> updateQuantity());
    }

    public void setProductId(String productId) {
        this.productId = productId;
        updateProduct();
        updateQuantity();
    }

    /** The product, kept live so stock changes show up immediately. Null if not found. */
    public LiveData<Product> getProduct() {
        return product;
    }

    public LiveData<Integer> getQuantityInCart() {
        return quantityInCart;
    }

    public LiveData<Event<Resource<Void>>> getAddResult() {
        return addResult;
    }

    public void addToCart() {
        Product current = product.getValue();
        if (current == null) return;
        Integer inCart = quantityInCart.getValue();
        if (inCart != null && inCart >= current.getStock()) {
            addResult.setValue(new Event<>(Resource.error(R.string.error_stock_limit, current.getStock())));
            return;
        }
        cartRepository.addToCart(uid, current.getId(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                addResult.setValue(new Event<>(Resource.success(null)));
            }

            @Override
            public void onError(String message) {
                addResult.setValue(new Event<>(Resource.error(message)));
            }
        });
    }

    private void updateProduct() {
        Resource<List<Product>> resource = products.getValue();
        if (productId == null || resource == null || resource.getData() == null) return;
        Product found = null;
        for (Product p : resource.getData()) {
            if (p.getId().equals(productId)) {
                found = p;
                break;
            }
        }
        product.setValue(found);
    }

    private void updateQuantity() {
        Resource<Map<String, Integer>> resource = cart.getValue();
        if (productId == null || resource == null || resource.getData() == null) return;
        Integer quantity = resource.getData().get(productId);
        quantityInCart.setValue(quantity != null ? quantity : 0);
    }
}
