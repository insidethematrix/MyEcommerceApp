package com.example.myecommerceapp.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myecommerceapp.R;
import com.example.myecommerceapp.ViewModelFactory;
import com.example.myecommerceapp.data.model.Clothing;
import com.example.myecommerceapp.data.model.Electronics;
import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.databinding.ActivityProductDetailBinding;
import com.example.myecommerceapp.ui.home.ProductAdapter;
import com.example.myecommerceapp.util.InsetsHelper;
import com.example.myecommerceapp.util.PriceFormatter;
import com.example.myecommerceapp.util.Resource;
import com.google.android.material.chip.Chip;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String EXTRA_PRODUCT_ID = "product_id";

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel viewModel;

    public static Intent newIntent(Context context, String productId) {
        return new Intent(context, ProductDetailActivity.class).putExtra(EXTRA_PRODUCT_ID, productId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsHelper.applySystemBarPadding(binding.getRoot(), true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this))
                .get(ProductDetailViewModel.class);
        viewModel.setProductId(getIntent().getStringExtra(EXTRA_PRODUCT_ID));

        binding.btnAddToCart.setOnClickListener(v -> viewModel.addToCart());

        viewModel.getProduct().observe(this, this::renderProduct);
        viewModel.getQuantityInCart().observe(this, quantity -> {
            binding.txtInCart.setText(quantity > 0 ? getString(R.string.detail_in_cart, quantity) : "");
        });
        viewModel.getAddResult().observe(this, event -> {
            Resource<Void> result = event.getContentIfNotHandled();
            if (result == null) return;
            String message = result.getStatus() == Resource.Status.SUCCESS
                    ? getString(R.string.detail_added)
                    : result.getError().resolve(this);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void renderProduct(Product product) {
        boolean found = product != null;
        binding.txtNotFound.setVisibility(found ? View.GONE : View.VISIBLE);
        binding.scroll.setVisibility(found ? View.VISIBLE : View.GONE);
        binding.bottomBar.setVisibility(found ? View.VISIBLE : View.GONE);
        if (!found) return;

        binding.toolbar.setTitle(product.getBrand());
        binding.txtBrand.setText(ProductAdapter.brandLabel(product));
        binding.txtName.setText(product.getName());
        binding.txtPrice.setText(PriceFormatter.format(product.getPrice()));
        binding.txtTaxInfo.setText(getString(R.string.detail_tax_info,
                product.getTaxRate().movePointRight(2).intValue()));
        binding.txtDescription.setText(product.getDescription());
        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(binding.imgProduct);
        ProductAdapter.bindBadge(binding.txtBadge, product, this);

        // Attributes differ per subclass: warranty for electronics, size/color for clothing
        binding.chipsAttributes.removeAllViews();
        if (product.isInStock()) {
            addAttribute(getString(R.string.detail_stock, product.getStock()));
        }
        if (product instanceof Electronics) {
            addAttribute(getString(R.string.detail_warranty, ((Electronics) product).getWarrantyMonths()));
        } else if (product instanceof Clothing) {
            Clothing clothing = (Clothing) product;
            addAttribute(getString(R.string.detail_size, clothing.getSize()));
            addAttribute(getString(R.string.detail_color, clothing.getColor()));
        }

        binding.btnAddToCart.setEnabled(product.isInStock());
        binding.btnAddToCart.setText(product.isInStock()
                ? R.string.action_add_to_cart : R.string.label_out_of_stock);
    }

    private void addAttribute(String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setClickable(false);
        binding.chipsAttributes.addView(chip);
    }
}
