package com.example.myecommerceapp.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myecommerceapp.MainActivity;
import com.example.myecommerceapp.R;
import com.example.myecommerceapp.ViewModelFactory;
import com.example.myecommerceapp.data.model.CartItem;
import com.example.myecommerceapp.data.model.Order;
import com.example.myecommerceapp.data.model.ShoppingCart;
import com.example.myecommerceapp.databinding.FragmentCartBinding;
import com.example.myecommerceapp.databinding.RowSummaryBinding;
import com.example.myecommerceapp.ui.detail.ProductDetailActivity;
import com.example.myecommerceapp.util.PriceFormatter;
import com.example.myecommerceapp.util.Resource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.math.BigDecimal;

public class CartFragment extends Fragment {
    private FragmentCartBinding binding;
    private CartViewModel viewModel;
    private CartAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Activity-scoped so it is shared with the cart badge in MainActivity
        viewModel = new ViewModelProvider(requireActivity(), new ViewModelFactory(requireContext()))
                .get(CartViewModel.class);

        adapter = new CartAdapter(new CartAdapter.Listener() {
            @Override
            public void onIncrement(CartItem item) {
                viewModel.increment(item);
            }

            @Override
            public void onDecrement(CartItem item) {
                viewModel.decrement(item);
            }

            @Override
            public void onRemove(CartItem item) {
                viewModel.remove(item);
            }

            @Override
            public void onOpen(CartItem item) {
                startActivity(ProductDetailActivity.newIntent(requireContext(), item.getProduct().getId()));
            }
        });
        binding.recyclerCart.setAdapter(adapter);

        binding.rowSubtotal.txtLabel.setText(R.string.cart_subtotal);
        binding.rowTax.txtLabel.setText(R.string.cart_tax);
        binding.rowDiscount.txtLabel.setText(R.string.cart_discount);

        binding.btnCheckout.setOnClickListener(v -> viewModel.checkout());
        binding.btnClear.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.cart_clear_confirm)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_clear_cart, (d, w) -> viewModel.clear())
                .show());
        binding.btnStartShopping.setOnClickListener(v ->
                ((MainActivity) requireActivity()).selectTab(R.id.nav_home));

        viewModel.getCart().observe(getViewLifecycleOwner(), this::render);
        viewModel.getCheckoutInProgress().observe(getViewLifecycleOwner(), inProgress -> {
            binding.btnCheckout.setEnabled(!inProgress);
            binding.btnCheckout.setText(inProgress ? R.string.checkout_in_progress : R.string.action_checkout);
        });
        viewModel.getCheckoutResult().observe(getViewLifecycleOwner(), event -> {
            Resource<String> result = event.getContentIfNotHandled();
            if (result == null) return;
            if (result.getStatus() == Resource.Status.SUCCESS) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setIcon(R.drawable.ic_receipt)
                        .setTitle(R.string.order_success)
                        .setMessage(getString(R.string.order_number, Order.shortId(result.getData())))
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            } else {
                toast(result.getError().resolve(requireContext()));
            }
        });
        viewModel.getMessages().observe(getViewLifecycleOwner(), event -> {
            Resource<Void> message = event.getContentIfNotHandled();
            if (message != null) toast(message.getError().resolve(requireContext()));
        });
    }

    private void render(Resource<ShoppingCart> resource) {
        boolean loading = resource.getStatus() == Resource.Status.LOADING;
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (resource.getStatus() == Resource.Status.ERROR) {
            // Hidden tabs stay started; don't surface errors (e.g. during logout) from them
            if (!isHidden()) toast(resource.getError().resolve(requireContext()));
            return;
        }
        ShoppingCart cart = resource.getData();
        boolean empty = cart == null || cart.isEmpty();
        boolean showEmpty = !loading && empty;

        binding.emptyState.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
        binding.summaryCard.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.btnClear.setVisibility(empty ? View.GONE : View.VISIBLE);
        adapter.submitList(cart != null ? cart.getItems() : null);
        if (empty) return;

        setRow(binding.rowSubtotal, PriceFormatter.format(cart.getSubtotal()));
        setRow(binding.rowTax, PriceFormatter.format(cart.getTaxTotal()));
        BigDecimal discount = cart.getDiscountTotal();
        binding.rowDiscount.getRoot().setVisibility(discount.signum() > 0 ? View.VISIBLE : View.GONE);
        setRow(binding.rowDiscount, getString(R.string.cart_discount_amount, PriceFormatter.format(discount)));
        binding.txtTotal.setText(PriceFormatter.format(cart.getTotal()));
    }

    private static void setRow(RowSummaryBinding row, String value) {
        row.txtValue.setText(value);
    }

    private void toast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
