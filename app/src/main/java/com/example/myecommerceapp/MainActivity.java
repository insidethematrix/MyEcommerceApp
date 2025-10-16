package com.example.myecommerceapp;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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

        Log.d("ECommerceTest", "--- Shopping Cart Testing Has Begun ---");
        List<Product> shoppingCart = new ArrayList<>();

        shoppingCart.add(new Electronics("ELC-001", "Laptop Pro X", 25000.0, "TechBrand", 24));
        shoppingCart.add(new Clothing("CLT-001", "Basic T-Shirt", 350.0, "M", "White"));
        shoppingCart.add(new Electronics("ELC-002", "Wireless Mouse", 750.0, "TechBrand", 12));

        for ( Product product :shoppingCart){
            double tax = product.calculateTax();
            Log.d("ECommerceTest", product.getName() + " has a tax of: " + tax + " TL");

        }
    }
}