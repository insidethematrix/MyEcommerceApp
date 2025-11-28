package com.example.myecommerceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private TextView txtEmail, txtId;
    private Button btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        // 2. Arayüzü Bağla
        txtEmail = findViewById(R.id.txtProfileEmail);
        txtId = findViewById(R.id.txtProfileId);
        btnLogout = findViewById(R.id.btnLogout);

        // 3. Kullanıcı Bilgilerini Çek ve Göster
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Bilgileri set et
            txtEmail.setText(currentUser.getEmail());
            txtId.setText(currentUser.getUid()); // Firebase'in verdiği benzersiz ID
        } else {
            txtEmail.setText("No user found");
        }
        btnLogout.setOnClickListener(v -> {
            // Firebase'den çıkış yap
            mAuth.signOut();

            // Giriş ekranına geri dön
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            // Geri tuşuna basınca tekrar profile dönmesin diye geçmişi temizle
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
}