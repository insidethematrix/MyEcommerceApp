package com.example.myecommerceapp;


import android.util.Log;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText inputProductName;
    private EditText inputProductPrice;

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
        inputProductName = findViewById(R.id.inputProductName);
        inputProductPrice = findViewById(R.id.inputProductPrice);

        currentUser = new User("Ahmet Eroğlu", "ahmet123", "ahmet@mail.com");
        cart = currentUser.getUserCard();

        btnAddElectronics.setOnClickListener(v -> {
            addProductFromInput("Electronics");
        });

        btnAddClothing.setOnClickListener(v -> {
            addProductFromInput("Clothing");
        });

        btnClear.setOnClickListener(v -> {
            cart.clearCart();
            txtReceipt.setText("Sepet temizlendi...\n");
            updateTotalPrice();

            // Kutuları da temizleyelim ki taze bir başlangıç olsun
            inputProductName.setText("");
            inputProductPrice.setText("");
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
    private void addProductFromInput(String type) {
        String nameText = inputProductName.getText().toString();
        String priceText = inputProductPrice.getText().toString();
        if (nameText.isEmpty() || priceText.isEmpty()) {
            Log.d("ECommerceTest", "Hata: İsim veya fiyat boş olamaz!");
            return;
        }
        double price = Double.parseDouble(priceText);
        if (type.equals("Electronics")) {
            // ID ve Marka şimdilik rastgele
            Electronics product = new Electronics("E-GEN", nameText, price, "MarkaYok", 12);
            cart.addProduct(product);
            updateReceipt(nameText + " (Elektronik) eklendi. Vergi: %20");
        }
        else if (type.equals("Clothing")) {
            // ID ve Beden şimdilik rastgele
            Clothing product = new Clothing("C-GEN", nameText, price, "L", "Siyah");
            cart.addProduct(product);
            updateReceipt(nameText + " (Giyim) eklendi. Vergi: %8");
        }
        updateTotalPrice();
        inputProductName.setText("");
        inputProductPrice.setText("");

    }
}