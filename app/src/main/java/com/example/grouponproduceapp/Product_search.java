package com.example.grouponproduceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List; // Import List here

public class Product_search extends AppCompatActivity {

    private EditText searchBar;
    private RecyclerView productRecyclerView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);

        // Initialize the views
        searchBar = findViewById(R.id.search_bar);
        productRecyclerView = findViewById(R.id.product_recycler_view);
        logoutButton = findViewById(R.id.back_button);

        // Enable the back button in the action bar (optional)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set the RecyclerView layout manager and adapter
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productRecyclerView.setAdapter(new productAdapter(this, getSampleProducts()));


        // Set the logout button listener to go back to the login page
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the login state in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.apply();

                // Navigate back to the login page (MainActivity)
                Intent intent = new Intent(Product_search.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the login page (MainActivity)
            Intent intent = new Intent(Product_search.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to create sample product list
    private List<product> getSampleProducts() {
        List<product> products = new ArrayList<>();
        products.add(new product("Organic Apples", "$3.99/lb", R.drawable.applesgdp, "Green Orchard Farm", "5 lb", "2023-12-01"));
        products.add(new product("Organic Bananas", "$1.29/lb", R.drawable.bananagdp, "Tropical Farms", "1 lb", "2023-11-20"));
        products.add(new product("Organic Carrots", "$2.99/lb", R.drawable.carrotsgdp, "Rooted Farm", "3 lb", "2023-12-15"));
        products.add(new product("Organic Blackberries", "$5.49/pint", R.drawable.blackberries, "Berry Bliss Farms", "1 pint", "2023-11-30"));
        products.add(new product("Organic Raspberries", "$5.99/pint", R.drawable.rasp, "Berry Bliss Farms", "1 pint", "2023-11-25"));
        products.add(new product("Organic Kiwi", "$0.99 each", R.drawable.kiwi, "Kiwi Valley", "1 each", "2023-12-10"));
        products.add(new product("Organic Lemons", "$1.99/lb", R.drawable.lmns, "Citrus Farms", "2 lb", "2024-01-05"));
        products.add(new product("Organic Limes", "$1.79/lb", R.drawable.lime, "Citrus Farms", "2 lb", "2024-01-10"));
        return products;
    }


}

