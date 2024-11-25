package com.example.grouponproduceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize views
        TextView productNameTextView = findViewById(R.id.product_name);
        TextView productPriceTextView = findViewById(R.id.product_price);
        ImageView productImageView = findViewById(R.id.product_image);
        TextView farmNameTextView = findViewById(R.id.farm_name);
        TextView quantityTextView = findViewById(R.id.quantity);
        TextView expiryDateTextView = findViewById(R.id.expiry_date);
        Button backToSearchButton = findViewById(R.id.back_to_search_button); // Back button

        // Retrieve data from Intent
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        int imageResId = getIntent().getIntExtra("imageResId", -1);
        String farmName = getIntent().getStringExtra("farmName");
        String quantity = getIntent().getStringExtra("quantity");
        String expiryDate = getIntent().getStringExtra("expiryDate");

        // Set data to views
        productNameTextView.setText(name);
        productPriceTextView.setText(price);
        if (imageResId != -1) {
            productImageView.setImageResource(imageResId);
        }
        farmNameTextView.setText(farmName);
        quantityTextView.setText(quantity);
        expiryDateTextView.setText(expiryDate);

        // Set up the back button to go to the search page
        backToSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to Product_search activity
                Intent intent = new Intent(ProductDetailActivity.this, Product_search.class);
                startActivity(intent);
                finish();  // Optionally call finish() to close the detail page
            }
        });
    }
}

