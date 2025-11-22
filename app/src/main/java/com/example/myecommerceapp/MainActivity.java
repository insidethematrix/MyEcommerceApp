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
    private Button btnAddElectronics;
    private Button btnAddClothing;
    private Button btnClear;
    private TextView txtReceipt;
    private TextView txtTotal;
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
        btnAddElectronics = findViewById(R.id.btnAddElectronics);
        btnAddClothing = findViewById(R.id.btnAddClothing);
        btnClear = findViewById(R.id.btnClear);
        txtReceipt = findViewById(R.id.txtReceipt);
        txtTotal = findViewById(R.id.txtTotal);

        currentUser = new User("Ahmet Eroğlu", "ahmet123", "ahmet@mail.com");
        cart = currentUser.getUserCard();

        btnAddElectronics.setOnClickListener(v -> {
            Electronics laptop = new Electronics("ELC-001", "Laptop Pro X", 25000.0, "TechBrand", 24);
            cart.addProduct(laptop);

            updateReceipt("Laptop added (tax %20)");
            updateTotalPrice();
        });

        btnAddClothing.setOnClickListener(v -> {
            Clothing tshirt = new Clothing("CLT-001", "T-Shirt", 350.0, "M", "White");
            cart.addProduct(tshirt);

            // Update UI
            updateReceipt("T-Shirt added (tax %8)");
            updateTotalPrice();
        });

        btnClear.setOnClickListener(v -> {
            cart.clearCart();

            // Reset UI
            txtReceipt.setText("Cart is cleaned...\n");
            updateTotalPrice();
        });






    }
    private void updateReceipt(String message) {
        // .append() adds text to the end instead of replacing it
        txtReceipt.append(message + "\n");
    }

    // Helper method to recalculate and show total price
    private void updateTotalPrice() {
        double total = cart.calculateTotalPrice();
        txtTotal.setText("Toplam: " + total + " TL");
    }



}