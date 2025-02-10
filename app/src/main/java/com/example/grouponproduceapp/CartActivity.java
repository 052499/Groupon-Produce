package com.example.grouponproduceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private ArrayList<Product> cartItems;
    private Button checkoutButton, clearCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_layout);

        recyclerView = findViewById(R.id.cart_recycler_view);
        checkoutButton = findViewById(R.id.checkout_button);
        clearCartButton = findViewById(R.id.clear_cart_button); // Button to clear cart

        cartItems = new ArrayList<>(CartManager.getInstance().getCartItems()); // Copy to prevent modification issues

        cartAdapter = new CartAdapter(this, cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        checkoutButton.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, PaymentActivity.class));
        });

        clearCartButton.setOnClickListener(v -> {
            CartManager.getInstance().clearCart();
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();
        });
    }
}
