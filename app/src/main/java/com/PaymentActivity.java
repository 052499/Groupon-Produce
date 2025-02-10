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

                // Initialize the pay button
                payButton = findViewById(R.id.pay_button);

                // Set an onClick listener to simulate the payment process
                payButton.setOnClickListener(v -> {
                        // Simulate a successful payment
                        simulatePayment();
                });
        }

        // Simulate the payment process
        private void simulatePayment() {
                // Display a success message
                Toast.makeText(PaymentActivity.this, "Payment Successful!", Toast.LENGTH_SHORT).show();

                // Redirect to SuccessActivity after the payment
                startActivity(new Intent(PaymentActivity.this, SuccessActivity.class));
                finish();  // Optionally finish this activity if you want to remove it from the stack
        }
}
