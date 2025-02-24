package com.example.grouponproduceapp;
import android.content.Intent; // Required for navigating between activities
import android.content.SharedPreferences; // Required for SharedPreferences to save card details
import android.os.Bundle; // Required for creating the activity
import android.view.View; // Required for handling button clicks
import android.widget.Button; // Required for the buttons
import android.widget.EditText; // Required for the EditText views
import android.widget.Toast; // Required for showing toast messages
import androidx.appcompat.app.AppCompatActivity; // Required for the AppCompatActivity class

public class PaymentActivity extends AppCompatActivity {

        private EditText cardNumber, expiryDate, cvv;
        private Button proceedButton, payButton;
        private SharedPreferences sharedPreferences;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_payment);

                // Initialize UI elements
                cardNumber = findViewById(R.id.card_number);
                expiryDate = findViewById(R.id.expiry_date);
                cvv = findViewById(R.id.cvv);
                proceedButton = findViewById(R.id.proceed_button);
                payButton = findViewById(R.id.pay_button);

                // Initialize SharedPreferences
                sharedPreferences = getSharedPreferences("PaymentData", MODE_PRIVATE);

                // Handle "Proceed" button click (store card details)
                proceedButton.setOnClickListener(view -> {
                        String cardNum = cardNumber.getText().toString().trim();
                        String expDate = expiryDate.getText().toString().trim();
                        String cvvCode = cvv.getText().toString().trim();

                        if (validateInput(cardNum, expDate, cvvCode)) {
                                // Store card details (for simulation only)
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("CardNumber", cardNum);
                                editor.putString("ExpiryDate", expDate);
                                editor.putString("CVV", cvvCode);
                                editor.apply();

                                // Notify user that the details are saved
                                Toast.makeText(PaymentActivity.this, "Card Details Stored Successfully!", Toast.LENGTH_SHORT).show();

                                // Navigate to SuccessActivity after storing card details
                                Intent successIntent = new Intent(PaymentActivity.this, SuccessActivity.class);
                                startActivity(successIntent); // Start SuccessActivity
                                finish(); // Optionally close the current activity (PaymentActivity)
                        } else {
                                Toast.makeText(PaymentActivity.this, "Invalid Card Details!", Toast.LENGTH_SHORT).show();
                        }
                });

                // Handle "Pay Now" button click (existing function)
                payButton.setOnClickListener(view -> simulatePayment());
        }

        // Validate card input (basic check for correct format)
        private boolean validateInput(String cardNum, String expDate, String cvvCode) {
                if (cardNum.length() != 16) {
                        Toast.makeText(this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show();
                        return false;
                }
                if (!expDate.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
                        Toast.makeText(this, "Expiry date must be in MM/YY format", Toast.LENGTH_SHORT).show();
                        return false;
                }
                if (cvvCode.length() != 3) {
                        Toast.makeText(this, "CVV must be 3 digits", Toast.LENGTH_SHORT).show();
                        return false;
                }
                return true;
        }


        // Simulate the payment process (existing function)
        private void simulatePayment() {
                Toast.makeText(this, "Payment Simulated Successfully!", Toast.LENGTH_SHORT).show();
        }
}
