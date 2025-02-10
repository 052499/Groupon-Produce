package com.example.grouponproduceapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {

    private TextView successMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // Initialize the success message TextView
        successMessage = findViewById(R.id.success_message);

        // Display a success message to the user
        successMessage.setText("Your order has been successfully placed!");
    }
}
