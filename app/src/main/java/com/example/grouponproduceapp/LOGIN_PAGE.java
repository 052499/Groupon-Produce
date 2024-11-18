package com.example.grouponproduceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.grouponproduceapp.R;

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
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Check if the email and password are correct (fetch stored credentials)
            if (isValidUser(email, password)) {
                Intent intent = new Intent(LOGIN_PAGE.this, Product_search.class); // Navigate to next activity
                startActivity(intent);
                finish(); // Close login page
            } else {
                Toast.makeText(LOGIN_PAGE.this, "Invalid credentials, please try again", Toast.LENGTH_SHORT).show();
            }
        });

        registerRedirect.setOnClickListener(v -> {
            // Redirect to registration page
            Intent intent = new Intent(LOGIN_PAGE.this, Registration_page.class);
            startActivity(intent);
        });
    }

    private boolean isValidUser(String email, String password) {
        // Fetch stored credentials from SharedPreferences
        String storedEmail = getSharedPreferences("user_details", MODE_PRIVATE).getString("email", null);
        String storedPassword = getSharedPreferences("user_details", MODE_PRIVATE).getString("password", null);

        // Check if the entered credentials match the stored ones
        return storedEmail != null && storedPassword != null &&
                storedEmail.equals(email) && storedPassword.equals(password);
    }
}
