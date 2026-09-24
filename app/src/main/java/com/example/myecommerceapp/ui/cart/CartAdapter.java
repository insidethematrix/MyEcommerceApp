package com.example.myecommerceapp.ui.cart;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myecommerceapp.R;
import com.example.myecommerceapp.data.model.CartItem;
import com.example.myecommerceapp.databinding.ItemCartBinding;
import com.example.myecommerceapp.util.PriceFormatter;

public class CartAdapter extends ListAdapter<CartItem, CartAdapter.ViewHolder> {

    public interface Listener {
        void onIncrement(CartItem item);

        void onDecrement(CartItem item);

        void onRemove(CartItem item);

        void onOpen(CartItem item);
    }

    private final Listener listener;

    public CartAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding binding;

        ViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CartItem item, Listener listener) {
            binding.txtName.setText(item.getProduct().getName());
            binding.txtUnitPrice.setText(binding.getRoot().getContext().getString(
                    R.string.cart_unit_price, PriceFormatter.format(item.getProduct().getPrice())));
            binding.txtQuantity.setText(String.valueOf(item.getQuantity()));
            binding.txtLineTotal.setText(PriceFormatter.format(item.getSubtotal()));
            Glide.with(binding.imgProduct)
                    .load(item.getProduct().getImageUrl())
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(binding.imgProduct);

            binding.btnIncrease.setEnabled(item.getQuantity() < item.getProduct().getStock());
            binding.btnIncrease.setOnClickListener(v -> listener.onIncrement(item));
            binding.btnDecrease.setOnClickListener(v -> listener.onDecrement(item));
            binding.btnRemove.setOnClickListener(v -> listener.onRemove(item));
            binding.getRoot().setOnClickListener(v -> listener.onOpen(item));
        }
    }

    private static final DiffUtil.ItemCallback<CartItem> DIFF = new DiffUtil.ItemCallback<CartItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.getProduct().getId().equals(newItem.getProduct().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CartItem oldItem, @NonNull CartItem newItem) {
            return oldItem.equals(newItem);
        }
    };
}
