package com.example.grouponproduceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Registration_page extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Registration_page.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Registration_page.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(Registration_page.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Password length check (min 6 characters)
            if (password.length() < 6) {
                Toast.makeText(Registration_page.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for at least one uppercase letter
            if (!password.matches(".*[A-Z].*")) {
                Toast.makeText(Registration_page.this, "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for at least one special character
            if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                Toast.makeText(Registration_page.this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(Registration_page.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register user with Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Send email verification
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            Toast.makeText(Registration_page.this, "Registration successful. Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Registration_page.this, LOGIN_PAGE.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(Registration_page.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(Registration_page.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
