package com.example.grouponproduceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.grouponproduceapp.R;

public class Registration_page extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Check if any field is empty
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Registration_page.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // User Registration Logic (could be saving data in SharedPreferences or database)
                registerUser(username, email, password);

                // Show success message
                Toast.makeText(Registration_page.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Redirect to the Login page after successful registration
                Intent intent = new Intent(Registration_page.this, LOGIN_PAGE.class);
                startActivity(intent);
                finish();  // Finish current activity to avoid back navigation
            }
        });
    }

    private void registerUser(String username, String email, String password) {
        // Save the user details to SharedPreferences, database, or backend API
        // For example, save email and password using SharedPreferences (simple implementation for demo):
        getSharedPreferences("user_details", MODE_PRIVATE)
                .edit()
                .putString("email", email)
                .putString("password", password)
                .apply();
    }
}

