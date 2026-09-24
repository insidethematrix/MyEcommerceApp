package com.example.myecommerceapp.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myecommerceapp.R;
import com.example.myecommerceapp.data.model.CartItem;
import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.data.model.ShoppingCart;
import com.example.myecommerceapp.data.repository.AuthRepository;
import com.example.myecommerceapp.data.repository.CartRepository;
import com.example.myecommerceapp.data.repository.OrderRepository;
import com.example.myecommerceapp.data.repository.ProductRepository;
import com.example.myecommerceapp.util.Event;
import com.example.myecommerceapp.util.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Joins the live catalog with the user's live cart (productId to quantity) into a
 * {@link ShoppingCart}. Scoped to MainActivity so the cart tab and the badge share it.
 */
public class CartViewModel extends ViewModel {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final String uid;
    private final LiveData<Resource<List<Product>>> products;
    private final LiveData<Resource<Map<String, Integer>>> quantities;

    private final MediatorLiveData<Resource<ShoppingCart>> cart = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> checkoutInProgress = new MutableLiveData<>(false);
    private final MutableLiveData<Event<Resource<String>>> checkoutResult = new MutableLiveData<>();
    private final MutableLiveData<Event<Resource<Void>>> messages = new MutableLiveData<>();

    public CartViewModel(ProductRepository productRepository, CartRepository cartRepository,
                         OrderRepository orderRepository, AuthRepository authRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.uid = authRepository.getCurrentUserId();
        this.products = productRepository.getProducts();
        this.quantities = cartRepository.observeCart(uid);
        cart.addSource(products, unused -> combine());
        cart.addSource(quantities, unused -> combine());
    }

    public LiveData<Resource<ShoppingCart>> getCart() {
        return cart;
    }

    public LiveData<Boolean> getCheckoutInProgress() {
        return checkoutInProgress;
    }

    /** One-shot result of a checkout; success carries the order id. */
    public LiveData<Event<Resource<String>>> getCheckoutResult() {
        return checkoutResult;
    }

    /** One-shot warnings such as hitting the stock limit. */
    public LiveData<Event<Resource<Void>>> getMessages() {
        return messages;
    }

    public void increment(CartItem item) {
        int stock = item.getProduct().getStock();
        if (item.getQuantity() >= stock) {
            messages.setValue(new Event<>(Resource.error(R.string.error_stock_limit, stock)));
            return;
        }
        cartRepository.setQuantity(uid, item.getProduct().getId(), item.getQuantity() + 1);
    }

    public void decrement(CartItem item) {
        cartRepository.setQuantity(uid, item.getProduct().getId(), item.getQuantity() - 1);
    }

    public void remove(CartItem item) {
        cartRepository.setQuantity(uid, item.getProduct().getId(), 0);
    }

    public void clear() {
        cartRepository.clearCart(uid);
    }

    public void checkout() {
        Resource<ShoppingCart> current = cart.getValue();
        if (current == null || current.getData() == null || current.getData().isEmpty()) return;
        if (Boolean.TRUE.equals(checkoutInProgress.getValue())) return;

        // Fast local check for a friendly message; the transaction re-checks on the server
        for (CartItem item : current.getData().getItems()) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                checkoutResult.setValue(new Event<>(
                        Resource.error(R.string.error_out_of_stock, item.getProduct().getName())));
                return;
            }
        }

        checkoutInProgress.setValue(true);
        orderRepository.placeOrder(uid, current.getData(), new OrderRepository.PlaceOrderCallback() {
            @Override
            public void onSuccess(String orderId) {
                checkoutInProgress.setValue(false);
                checkoutResult.setValue(new Event<>(Resource.success(orderId)));
            }

            @Override
            public void onOutOfStock(String productName) {
                checkoutInProgress.setValue(false);
                checkoutResult.setValue(new Event<>(Resource.error(R.string.error_out_of_stock, productName)));
            }

            @Override
            public void onError(String message) {
                checkoutInProgress.setValue(false);
                checkoutResult.setValue(new Event<>(Resource.error(message)));
            }
        });
    }

    private void combine() {
        Resource<List<Product>> productRes = products.getValue();
        Resource<Map<String, Integer>> cartRes = quantities.getValue();
        if (productRes == null || cartRes == null) return;

        if (productRes.getStatus() == Resource.Status.ERROR) {
            cart.setValue(Resource.error(productRes.getError().getText()));
        } else if (cartRes.getStatus() == Resource.Status.ERROR) {
            cart.setValue(Resource.error(cartRes.getError().getText()));
        } else if (productRes.getData() == null || cartRes.getData() == null) {
            cart.setValue(Resource.loading());
        } else {
            cart.setValue(Resource.success(buildCart(productRes.getData(), cartRes.getData())));
        }
    }

    /** Products that were deleted from the catalog are silently dropped from the cart. */
    static ShoppingCart buildCart(List<Product> catalog, Map<String, Integer> quantities) {
        Map<String, Product> byId = new HashMap<>();
        for (Product product : catalog) {
            byId.put(product.getId(), product);
        }
        List<CartItem> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            Product product = byId.get(entry.getKey());
            if (product != null && entry.getValue() > 0) {
                items.add(new CartItem(product, entry.getValue()));
            }
        }
        return new ShoppingCart(items);
    }
}
