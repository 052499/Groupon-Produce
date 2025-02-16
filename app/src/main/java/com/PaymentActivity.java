package com.example.grouponproduceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private Button payButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initializeUI();
    }

    private void initializeUI() {
        payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        showToast("Payment Successful!");
        navigateToSuccessScreen();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToSuccessScreen() {
        Intent intent = new Intent(this, SuccessActivity.class);
        startActivity(intent);
        finish();
    }
}

