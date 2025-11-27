package com.example.myecommerceapp;


import android.util.Log;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ImageButton btnLaptop, btnMouse, btnTshirt, btnJeans;
    private Button btnClear;
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
        btnLaptop = findViewById(R.id.btnLaptop);
        btnMouse = findViewById(R.id.btnMouse);
        btnTshirt = findViewById(R.id.btnTshirt);
        btnJeans = findViewById(R.id.btnJeans);
        btnClear = findViewById(R.id.btnClear);
        txtTotal = findViewById(R.id.txtTotal);

        String incomingUsername = getIntent().getStringExtra("USERNAME_KEY");
        if (incomingUsername == null) incomingUsername = "Misafir";
        currentUser = new User(incomingUsername, "user1", "email@test.com");
        cart = currentUser.getUserCard();

        btnLaptop.setOnClickListener(v -> {
            Electronics laptop = new Electronics("ELC-001", "Laptop Pro", 25000.0, "TechBrand", 24);
            addToCart(laptop);
        });
        btnMouse.setOnClickListener(v -> {
            Electronics mouse = new Electronics("ELC-002", "Wireless Mouse", 750.0, "TechBrand", 12);
            addToCart(mouse);
        });

        // T-Shirt'e Tıklanınca
        btnTshirt.setOnClickListener(v -> {
            Clothing tshirt = new Clothing("CLT-001", "Basic T-Shirt", 350.0, "M", "White");
            addToCart(tshirt);
        });

        // Jeans'e Tıklanınca
        btnJeans.setOnClickListener(v -> {
            Clothing jeans = new Clothing("CLT-002", "Blue Jeans", 800.0, "32", "Blue");
            addToCart(jeans);
        });

        // Temizle Butonu
        btnClear.setOnClickListener(v -> {
            cart.clearCart();
            updateTotalPrice();
            Toast.makeText(this, "Cart emptied!", Toast.LENGTH_SHORT).show();

        });


    }
    private void addToCart(Product product) {
        cart.addProduct(product);
        updateTotalPrice();

        // Kullanıcıya küçük bir bilgi balonu göster (Feedback)
        Toast.makeText(this, product.getName() + " Added to cart!", Toast.LENGTH_SHORT).show();
    }

    // Helper method to recalculate and show total price
    private void updateTotalPrice() {
        double total = cart.calculateTotalPrice();
        txtTotal.setText("Total: " + total + " TL");
    }


    }
