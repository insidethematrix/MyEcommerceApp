package com.example.myecommerceapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Toast;
public class LoginActivity extends AppCompatActivity {
    private EditText inputUsername;
    private EditText inputPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            // Kutulardaki yazıları al
            String username = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();


            if (username.equals("admin") && password.equals("1234")) {


                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                intent.putExtra("USERNAME_KEY", username);
                startActivity(intent);

                finish();

            } else {

                Toast.makeText(this, "Hatalı Kullanıcı Adı veya Şifre!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}