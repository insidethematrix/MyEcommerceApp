package com.example.myecommerceapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myecommerceapp.data.model.Order;
import com.example.myecommerceapp.data.repository.AuthRepository;
import com.example.myecommerceapp.data.repository.OrderRepository;
import com.example.myecommerceapp.util.Resource;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final LiveData<Resource<List<Order>>> orders;

    public ProfileViewModel(AuthRepository authRepository, OrderRepository orderRepository) {
        this.authRepository = authRepository;
        this.orders = orderRepository.observeOrders(authRepository.getCurrentUserId());
    }

    public String getName() {
        return authRepository.getCurrentUserName();
    }

    public String getEmail() {
        return authRepository.getCurrentUserEmail();
    }

    public LiveData<Resource<List<Order>>> getOrders() {
        return orders;
    }

    public void logout() {
        authRepository.logout();
    }
}
