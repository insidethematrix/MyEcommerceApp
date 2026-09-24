package com.example.myecommerceapp.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myecommerceapp.R;
import com.example.myecommerceapp.data.model.Order;
import com.example.myecommerceapp.databinding.ItemOrderBinding;
import com.example.myecommerceapp.util.PriceFormatter;

import java.text.DateFormat;

public class OrderAdapter extends ListAdapter<Order, OrderAdapter.ViewHolder> {

    public OrderAdapter() {
        super(DIFF);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderBinding binding;

        ViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Order order) {
            Context context = binding.getRoot().getContext();
            binding.txtOrderNumber.setText(context.getString(R.string.order_number, order.getShortId()));
            binding.txtStatus.setText(R.string.order_status_placed);
            binding.txtDate.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(order.getCreatedAt()));
            binding.txtItemCount.setText(context.getResources().getQuantityString(
                    R.plurals.order_item_count, order.getItemCount(), order.getItemCount()));
            binding.txtTotal.setText(PriceFormatter.format(order.getTotal()));

            StringBuilder summary = new StringBuilder();
            for (Order.Line line : order.getLines()) {
                if (summary.length() > 0) summary.append(", ");
                summary.append(line.getName()).append(" ×").append(line.getQuantity());
            }
            binding.txtItems.setText(summary);
        }
    }

    private static final DiffUtil.ItemCallback<Order> DIFF = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            // Orders are immutable, apart from the server timestamp resolving after a write
            return oldItem.equals(newItem) && oldItem.getCreatedAt().equals(newItem.getCreatedAt());
        }
    };
}
