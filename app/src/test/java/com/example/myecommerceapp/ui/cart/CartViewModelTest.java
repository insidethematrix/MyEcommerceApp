package com.example.myecommerceapp.ui.cart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.myecommerceapp.R;
import com.example.myecommerceapp.TestData;
import com.example.myecommerceapp.data.model.CartItem;
import com.example.myecommerceapp.data.model.ShoppingCart;
import com.example.myecommerceapp.data.repository.AuthRepository;
import com.example.myecommerceapp.fakes.FakeCartRepository;
import com.example.myecommerceapp.fakes.FakeOrderRepository;
import com.example.myecommerceapp.fakes.FakeProductRepository;
import com.example.myecommerceapp.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class CartViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    private FakeProductRepository products;
    private FakeCartRepository cart;
    private FakeOrderRepository orders;
    private CartViewModel viewModel;

    @Before
    public void setUp() {
        products = new FakeProductRepository();
        cart = new FakeCartRepository();
        orders = new FakeOrderRepository();
        AuthRepository auth = mock(AuthRepository.class);
        when(auth.getCurrentUserId()).thenReturn("user-1");

        viewModel = new CartViewModel(products, cart, orders, auth);
        viewModel.getCart().observeForever(unused -> {
        });
        // Laptop has 5 in stock, t-shirt has 10
        products.emit(Arrays.asList(TestData.laptop(), TestData.tshirt()));
    }

    @Test
    public void isLoading_untilCartArrives() {
        assertEquals(Resource.Status.LOADING, viewModel.getCart().getValue().getStatus());
    }

    @Test
    public void joinsCatalogWithQuantities() {
        cart.emit(quantities("elc-laptop", 1, "clt-tshirt", 2));

        ShoppingCart result = viewModel.getCart().getValue().getData();
        assertEquals(2, result.getItems().size());
        assertEquals(3, result.getItemCount());
        assertEquals(new BigDecimal("28256.00"), result.getTotal());
    }

    @Test
    public void productsRemovedFromCatalog_areDropped() {
        cart.emit(quantities("elc-laptop", 1, "deleted-product", 4));

        assertEquals(1, viewModel.getCart().getValue().getData().getItemCount());
    }

    @Test
    public void increment_belowStock_updatesQuantity() {
        cart.emit(quantities("clt-tshirt", 2));

        viewModel.increment(firstItem());

        assertEquals(Integer.valueOf(3), cart.quantityOf("clt-tshirt"));
    }

    @Test
    public void increment_atStockLimit_showsMessageInsteadOfWriting() {
        cart.emit(quantities("elc-laptop", 5));
        int writesBefore = cart.writeCount;

        viewModel.increment(firstItem());

        assertEquals(writesBefore, cart.writeCount);
        Resource<Void> message = viewModel.getMessages().getValue().getContentIfNotHandled();
        assertEquals(R.string.error_stock_limit, message.getError().getRes());
    }

    @Test
    public void decrement_toZero_removesLine() {
        cart.emit(quantities("clt-tshirt", 1));

        viewModel.decrement(firstItem());

        assertNull(cart.quantityOf("clt-tshirt"));
        assertTrue(viewModel.getCart().getValue().getData().isEmpty());
    }

    @Test
    public void checkout_success_reportsOrderId() {
        cart.emit(quantities("elc-laptop", 1));

        viewModel.checkout();
        assertTrue(viewModel.getCheckoutInProgress().getValue());
        orders.pendingCallback.onSuccess("abc123");

        assertFalse(viewModel.getCheckoutInProgress().getValue());
        Resource<String> result = viewModel.getCheckoutResult().getValue().getContentIfNotHandled();
        assertEquals("abc123", result.getData());
        assertEquals(1, orders.placedCart.getItemCount());
    }

    @Test
    public void checkout_whileInProgress_isIgnored() {
        cart.emit(quantities("elc-laptop", 1));

        viewModel.checkout();
        viewModel.checkout();

        assertEquals(1, orders.placeOrderCalls);
    }

    @Test
    public void checkout_serverOutOfStock_namesTheProduct() {
        cart.emit(quantities("elc-laptop", 1));

        viewModel.checkout();
        orders.pendingCallback.onOutOfStock("Laptop Pro");

        Resource<String> result = viewModel.getCheckoutResult().getValue().getContentIfNotHandled();
        assertEquals(R.string.error_out_of_stock, result.getError().getRes());
        assertEquals("Laptop Pro", result.getError().getArgs()[0]);
    }

    @Test
    public void checkout_quantityAboveStock_failsBeforeReachingServer() {
        // Stock dropped to 5 after the user put 6 in the cart
        cart.emit(quantities("elc-laptop", 6));

        viewModel.checkout();

        assertEquals(0, orders.placeOrderCalls);
        Resource<String> result = viewModel.getCheckoutResult().getValue().getContentIfNotHandled();
        assertEquals(R.string.error_out_of_stock, result.getError().getRes());
    }

    @Test
    public void checkout_emptyCart_doesNothing() {
        cart.emit(new LinkedHashMap<>());

        viewModel.checkout();

        assertEquals(0, orders.placeOrderCalls);
    }

    private CartItem firstItem() {
        return viewModel.getCart().getValue().getData().getItems().get(0);
    }

    private static Map<String, Integer> quantities(Object... pairs) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String) pairs[i], (Integer) pairs[i + 1]);
        }
        return map;
    }
}
