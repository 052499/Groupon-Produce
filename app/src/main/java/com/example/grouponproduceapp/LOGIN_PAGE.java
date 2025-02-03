package com.example.grouponproduceapp;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LOGIN_PAGE extends AppCompatActivity {

    private static final String TAG = "LOGIN_PAGE";

    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        TextView registerRedirect = findViewById(R.id.register_redirect);
        TextView forgotPassword = findViewById(R.id.forgot_password);

        // Handle login button click
        loginButton.setOnClickListener(v -> handleLogin());

        // Redirect to registration page
        registerRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(LOGIN_PAGE.this, Registration_page.class);
            startActivity(intent);
        });

        // Redirect to forgot password page
        forgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    /**
     * Handles user login
     */
    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            // Navigate to the next activity
                            Intent intent = new Intent(LOGIN_PAGE.this, Product_search.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Login failed", task.getException());
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Redirects to Forgot Password activity
     */
    private void handleForgotPassword() {
        Intent intent = new Intent(LOGIN_PAGE.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
