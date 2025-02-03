package com.example.grouponproduceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productPrice, farmName, quantity, expiryDate;
    private Button addToCartButton, backButton, viewCartButton;
    private DatabaseReference cartRef;
    private FirebaseAuth auth;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize views
        productImage = findViewById(R.id.product_detail_image);
        productName = findViewById(R.id.product_detail_name);
        productPrice = findViewById(R.id.product_detail_price);
        farmName = findViewById(R.id.product_detail_farm);
        quantity = findViewById(R.id.product_detail_quantity);
        expiryDate = findViewById(R.id.product_detail_expiry);
        addToCartButton = findViewById(R.id.add_to_cart_button);
        backButton = findViewById(R.id.back_button);
        viewCartButton = findViewById(R.id.view_cart_button);

        // Firebase reference
        auth = FirebaseAuth.getInstance();
        cartRef = FirebaseDatabase.getInstance().getReference("cart").child(auth.getCurrentUser().getUid());

        // Get product details from intent
        Intent intent = getIntent();
        if (intent != null) {
            productName.setText(intent.getStringExtra("name"));
            productPrice.setText(intent.getStringExtra("productPrice"));
            farmName.setText("Farm: " + intent.getStringExtra("farmName"));
            quantity.setText("Quantity: " + intent.getStringExtra("quantity"));
            expiryDate.setText("Expiry Date: " + intent.getStringExtra("expiryDate"));

            if (intent.hasExtra("imageUrl")) {
                imageUrl = intent.getStringExtra("imageUrl");
                Glide.with(this).load(imageUrl).into(productImage);
            }
        }

        // Add to Cart Button
        addToCartButton.setOnClickListener(v -> {
            // Get product details from the UI
            String name = productName.getText().toString();
            String price = productPrice.getText().toString();
            String farm = farmName.getText().toString().replace("Farm: ", ""); // Remove prefix
            String quantityText = quantity.getText().toString().replace("Quantity: ", "");
            String expiry = expiryDate.getText().toString().replace("Expiry Date: ", "");

            // Get the image resource ID (if using a drawable)
            int imageResId = R.drawable.added; // Use your drawable resource here

            // Create Product object with all required parameters
            Product product = new Product(
                    null, // key, not needed for now
                    name,
                    price,
                    imageResId, // Pass the drawable resource ID
                    farm,
                    quantityText,
                    expiry
            );

            // Add to cart in Firebase
            cartRef.push().setValue(product);
            Toast.makeText(ProductDetailActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
        });

        // Back Button
        backButton.setOnClickListener(v -> finish());

        // View Cart Button
        viewCartButton.setOnClickListener(v -> {
            Intent cartIntent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(cartIntent);
        });
    }
}
