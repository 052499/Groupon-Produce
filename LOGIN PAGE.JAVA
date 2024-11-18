package com.example.grouponproduceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grouponproduceapp.R;
import com.example.grouponproduceapp.Registration_page;


public class LOGIN_PAGE extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerRedirect = findViewById(R.id.register_redirect);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (isValidUser(email, password)) {
                Intent intent = new Intent(LOGIN_PAGE.this, Product_search.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LOGIN_PAGE.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        registerRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(LOGIN_PAGE.this, Registration_page.class);
            startActivity(intent);
        });
    }
