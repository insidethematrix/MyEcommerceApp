package com.example.myecommerceapp;

import com.example.myecommerceapp.data.repository.AuthRepository;
import com.example.myecommerceapp.data.repository.CartRepository;
import com.example.myecommerceapp.data.repository.FirebaseAuthRepository;
import com.example.myecommerceapp.data.repository.FirestoreCartRepository;
import com.example.myecommerceapp.data.repository.FirestoreOrderRepository;
import com.example.myecommerceapp.data.repository.FirestoreProductRepository;
import com.example.myecommerceapp.data.repository.OrderRepository;
import com.example.myecommerceapp.data.repository.ProductRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Manual dependency injection: creates the app-wide repository singletons in one place.
 * ViewModels receive repository interfaces, so tests can swap in fakes.
 */
public class AppContainer {
    public final AuthRepository authRepository;
    public final ProductRepository productRepository;
    public final CartRepository cartRepository;
    public final OrderRepository orderRepository;

    public AppContainer() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        authRepository = new FirebaseAuthRepository(FirebaseAuth.getInstance());
        productRepository = new FirestoreProductRepository(db);
        cartRepository = new FirestoreCartRepository(db);
        orderRepository = new FirestoreOrderRepository(db);
    }
}
