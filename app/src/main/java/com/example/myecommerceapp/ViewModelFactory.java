package com.example.myecommerceapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.myecommerceapp.ui.auth.AuthViewModel;
import com.example.myecommerceapp.ui.cart.CartViewModel;
import com.example.myecommerceapp.ui.detail.ProductDetailViewModel;
import com.example.myecommerceapp.ui.home.HomeViewModel;
import com.example.myecommerceapp.ui.profile.ProfileViewModel;

/** Creates ViewModels with their repository dependencies from the {@link AppContainer}. */
public class ViewModelFactory implements ViewModelProvider.Factory {
    private final AppContainer c;

    public ViewModelFactory(Context context) {
        this.c = AvoraApp.container(context);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == AuthViewModel.class) {
            return (T) new AuthViewModel(c.authRepository);
        } else if (modelClass == HomeViewModel.class) {
            return (T) new HomeViewModel(c.productRepository);
        } else if (modelClass == ProductDetailViewModel.class) {
            return (T) new ProductDetailViewModel(c.productRepository, c.cartRepository, c.authRepository);
        } else if (modelClass == CartViewModel.class) {
            return (T) new CartViewModel(c.productRepository, c.cartRepository, c.orderRepository, c.authRepository);
        } else if (modelClass == ProfileViewModel.class) {
            return (T) new ProfileViewModel(c.authRepository, c.orderRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass.getName());
    }
}
