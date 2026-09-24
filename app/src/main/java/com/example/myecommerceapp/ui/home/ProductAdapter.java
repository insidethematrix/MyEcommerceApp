package com.example.myecommerceapp.ui.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myecommerceapp.R;
import com.example.myecommerceapp.data.model.Discountable;
import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.databinding.ItemProductBinding;
import com.example.myecommerceapp.util.PriceFormatter;
import com.google.android.material.color.MaterialColors;

import java.util.Locale;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        ViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product, OnProductClickListener listener) {
            Context context = binding.getRoot().getContext();
            binding.txtBrand.setText(brandLabel(product));
            binding.txtName.setText(product.getName());
            binding.txtPrice.setText(PriceFormatter.format(product.getPrice()));
            Glide.with(binding.imgProduct)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_image)
                    .error(R.drawable.ic_image)
                    .into(binding.imgProduct);
            bindBadge(binding.txtBadge, product, context);
            binding.getRoot().setAlpha(product.isInStock() ? 1f : 0.6f);
            binding.getRoot().setOnClickListener(v -> listener.onProductClick(product));
        }
    }

    /**
     * Brand names are proper nouns, so they are uppercased with Locale.ROOT; textAllCaps would
     * use the device locale and turn "Nike" into "NİKE" on Turkish phones.
     */
    public static String brandLabel(Product product) {
        return product.getBrand().toUpperCase(Locale.ROOT);
    }

    /** Shared with the detail screen: shows "Out of stock" or the discount, if any. */
    public static void bindBadge(TextView badge, Product product, Context context) {
        if (!product.isInStock()) {
            badge.setText(R.string.label_out_of_stock);
            // Inverse surface colors keep the label readable in both light and dark mode
            badge.setBackgroundTintList(ColorStateList.valueOf(MaterialColors.getColor(
                    badge, com.google.android.material.R.attr.colorOnSurfaceVariant)));
            badge.setTextColor(MaterialColors.getColor(
                    badge, com.google.android.material.R.attr.colorSurface));
            badge.setVisibility(View.VISIBLE);
        } else if (product instanceof Discountable) {
            badge.setText(context.getString(R.string.label_discount,
                    ((Discountable) product).getDiscountPercent()));
            badge.setBackgroundTintList(null);
            badge.setTextColor(ContextCompat.getColor(context, R.color.white));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    private static final DiffUtil.ItemCallback<Product> DIFF = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    };
}
