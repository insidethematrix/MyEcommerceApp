package com.example.myecommerceapp.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.myecommerceapp.BuildConfig;
import com.example.myecommerceapp.R;
import com.example.myecommerceapp.ViewModelFactory;
import com.example.myecommerceapp.data.SampleDataLoader;
import com.example.myecommerceapp.data.model.Clothing;
import com.example.myecommerceapp.data.model.Electronics;
import com.example.myecommerceapp.data.model.Product;
import com.example.myecommerceapp.databinding.FragmentHomeBinding;
import com.example.myecommerceapp.ui.detail.ProductDetailActivity;
import com.example.myecommerceapp.util.Resource;

import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(HomeViewModel.class);

        adapter = new ProductAdapter(product ->
                startActivity(ProductDetailActivity.newIntent(requireContext(), product.getId())));
        binding.recyclerProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerProducts.setAdapter(adapter);

        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setQuery(s.toString());
            }
        });

        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int checked = checkedIds.isEmpty() ? View.NO_ID : checkedIds.get(0);
            if (checked == R.id.chipElectronics) {
                viewModel.setCategory(Electronics.CATEGORY);
            } else if (checked == R.id.chipClothing) {
                viewModel.setCategory(Clothing.CATEGORY);
            } else {
                viewModel.setCategory(null);
            }
        });

        binding.btnSeed.setOnClickListener(v -> seedSampleData());

        viewModel.getProducts().observe(getViewLifecycleOwner(), this::render);
        viewModel.getSeedResult().observe(getViewLifecycleOwner(), event -> {
            Resource<Void> result = event.getContentIfNotHandled();
            if (result == null) return;
            binding.btnSeed.setEnabled(true);
            String message = result.getStatus() == Resource.Status.SUCCESS
                    ? getString(R.string.seed_success)
                    : result.getError().resolve(requireContext());
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
    }

    private void render(Resource<List<Product>> resource) {
        boolean loading = resource.getStatus() == Resource.Status.LOADING;
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);

        if (resource.getStatus() == Resource.Status.ERROR) {
            adapter.submitList(null);
            showEmpty(resource.getError().resolve(requireContext()), false);
            return;
        }
        if (loading) {
            binding.emptyState.setVisibility(View.GONE);
            return;
        }

        List<Product> products = resource.getData();
        adapter.submitList(products);
        if (products.isEmpty()) {
            boolean filtered = viewModel.hasActiveFilter();
            // The seed button is a developer convenience and never ships in release builds
            showEmpty(getString(filtered ? R.string.home_no_results : R.string.home_empty),
                    !filtered && BuildConfig.DEBUG);
        } else {
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    private void showEmpty(String message, boolean showSeed) {
        binding.txtEmpty.setText(message);
        binding.btnSeed.setVisibility(showSeed ? View.VISIBLE : View.GONE);
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    private void seedSampleData() {
        try {
            binding.btnSeed.setEnabled(false);
            viewModel.seed(SampleDataLoader.load(requireContext()));
        } catch (Exception e) {
            binding.btnSeed.setEnabled(true);
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
