package com.example.myecommerceapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myecommerceapp.R;
import com.example.myecommerceapp.ViewModelFactory;
import com.example.myecommerceapp.data.model.Order;
import com.example.myecommerceapp.databinding.FragmentProfileBinding;
import com.example.myecommerceapp.ui.auth.LoginActivity;
import com.example.myecommerceapp.util.Resource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(requireContext()))
                .get(ProfileViewModel.class);

        String email = viewModel.getEmail();
        String name = TextUtils.isEmpty(viewModel.getName()) ? email : viewModel.getName();
        binding.txtName.setText(name);
        binding.txtEmail.setText(email);
        binding.txtAvatar.setText(TextUtils.isEmpty(name)
                ? "?" : name.substring(0, 1).toUpperCase(Locale.getDefault()));

        adapter = new OrderAdapter();
        binding.recyclerOrders.setAdapter(adapter);

        binding.btnLogout.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.logout_confirm)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_logout, (d, w) -> logout())
                .show());

        viewModel.getOrders().observe(getViewLifecycleOwner(), this::render);
    }

    private void render(Resource<List<Order>> resource) {
        boolean loading = resource.getStatus() == Resource.Status.LOADING;
        binding.progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (resource.getStatus() == Resource.Status.ERROR) {
            binding.txtEmpty.setText(resource.getError().resolve(requireContext()));
            binding.emptyState.setVisibility(View.VISIBLE);
            return;
        }
        List<Order> orders = resource.getData();
        adapter.submitList(orders);
        boolean empty = !loading && (orders == null || orders.isEmpty());
        binding.txtEmpty.setText(R.string.profile_no_orders);
        binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void logout() {
        viewModel.logout();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        // Clear the back stack so Back can't return to the signed-in screens
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
