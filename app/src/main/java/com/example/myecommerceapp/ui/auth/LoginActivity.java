package com.example.myecommerceapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myecommerceapp.MainActivity;
import com.example.myecommerceapp.ViewModelFactory;
import com.example.myecommerceapp.databinding.ActivityLoginBinding;
import com.example.myecommerceapp.util.InsetsHelper;
import com.example.myecommerceapp.util.Resource;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(AuthViewModel.class);

        // Firebase keeps the session across restarts, so skip straight to the store
        if (viewModel.isLoggedIn()) {
            openMain();
            return;
        }

        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsHelper.applySystemBarPadding(binding.getRoot(), true);

        binding.btnLogin.setOnClickListener(v -> submit());
        binding.inputPassword.setOnEditorActionListener((v, actionId, event) -> {
            submit();
            return true;
        });
        binding.btnGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        viewModel.getAuthState().observe(this, this::render);
    }

    private void submit() {
        viewModel.login(
                String.valueOf(binding.inputEmail.getText()),
                String.valueOf(binding.inputPassword.getText()));
    }

    private void render(Resource<Void> state) {
        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.progress.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        binding.btnLogin.setEnabled(!loading);

        if (state.getStatus() == Resource.Status.ERROR) {
            binding.txtError.setText(state.getError().resolve(this));
            binding.txtError.setVisibility(View.VISIBLE);
        } else {
            binding.txtError.setVisibility(View.GONE);
        }

        if (state.getStatus() == Resource.Status.SUCCESS) {
            openMain();
        }
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
