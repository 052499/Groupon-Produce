package com.example.grouponproduceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationPage extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        initializeViews();
        setupRegisterButton();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.register_button);
    }

    private void setupRegisterButton() {
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (areFieldsValid(username, email, password)) {
                registerUser(email, password);
                showToast("Registration successful");
                navigateToLoginPage();
            } else {
                showToast("Please fill in all fields");
            }
        });
    }

    private boolean areFieldsValid(String username, String email, String password) {
        return !username.isEmpty() && !email.isEmpty() && !password.isEmpty();
    }

    private void registerUser(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(RegistrationPage.this, message, Toast.LENGTH_SHORT).show();
    }
}
