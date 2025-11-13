package com.example.myecommerceapp;


import android.util.Log;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button addProductButton;
    private TextView totalPriceTextView;
    private User currentUser;
    private ShoppingCart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addProductButton = findViewById(R.id.addProductButton);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        currentUser = new User("Ahmet Eroğlu", "ahmet123", "ahmet@mail.com");
        cart = currentUser.getUserCard();

        addProductButton.setOnClickListener(v -> {
            cart.clearCart();
            Log.d("ECommerceTest", "Cart cleared.");

            Electronics laptop = new Electronics("ELC-001", "Laptop Pro X", 25000.0, "TechBrand", 24);
            Clothing tshirt = new Clothing("CLT-001", "Basic T-Shirt", 350.0, "M", "White");
            Electronics mouse = new Electronics("ELC-002", "Wireless Mouse", 750.0, "TechBrand", 12);
            cart.addProduct(laptop);
            cart.addProduct(tshirt);
            cart.addProduct(mouse);
            Log.d("ECommerceTest", "New products added to cart!");

            cart.removeProduct(mouse);
            Log.d("ECommerceTest", "Mouse removed from the cart");



            double newTotal = cart.calculateTotalPrice();

            totalPriceTextView.setText("Total: " + String.valueOf(newTotal)+ " TL");
        });




    }
}