package com.example.myecommerceapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myecommerceapp.MainActivity;
import com.example.myecommerceapp.ViewModelFactory;
import com.example.myecommerceapp.databinding.ActivityRegisterBinding;
import com.example.myecommerceapp.util.InsetsHelper;
import com.example.myecommerceapp.util.Resource;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsHelper.applySystemBarPadding(binding.getRoot(), true);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> submit());
        binding.inputPassword.setOnEditorActionListener((v, actionId, event) -> {
            submit();
            return true;
        });
        binding.btnGoToLogin.setOnClickListener(v -> finish());

        viewModel.getAuthState().observe(this, this::render);
    }

    private void submit() {
        viewModel.register(
                String.valueOf(binding.inputName.getText()),
                String.valueOf(binding.inputEmail.getText()),
                String.valueOf(binding.inputPassword.getText()));
    }

    private void render(Resource<Void> state) {
        boolean loading = state.getStatus() == Resource.Status.LOADING;
        binding.progress.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
        binding.btnRegister.setEnabled(!loading);

        if (state.getStatus() == Resource.Status.ERROR) {
            binding.txtError.setText(state.getError().resolve(this));
            binding.txtError.setVisibility(View.VISIBLE);
        } else {
            binding.txtError.setVisibility(View.GONE);
        }

        if (state.getStatus() == Resource.Status.SUCCESS) {
            startActivity(new Intent(this, MainActivity.class));
            // Close the login screen too, so Back doesn't return to it
            finishAffinity();
        }
    }
}
