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

            cart.addProduct(new Clothing("CLT-002", "Designer Jeans", 1200.0, "L", "Blue"));
            Log.d("ECommerceTest", "Yeni ürün sepete eklendi!");

            double newTotal = cart.calculateTotalPrice();

            totalPriceTextView.setText("Total: " + String.valueOf(newTotal)+ " TL");
        });




    }
}