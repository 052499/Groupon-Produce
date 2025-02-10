package com.example.grouponproduceapp;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductManager {

    private DatabaseReference productsRef;

    public ProductManager() {
        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productsRef = database.getReference("products");
    }

    // Method to push product data to Firebase
    public void pushProductsToFirebase() {
        // Create product data using the constructor
        Product product1 = new Product("Organic Apples", "$3.99/lb", "applesgdp.jpg", "Green Orchard Farm", "5 lb", "2023-12-01");
        Product product2 = new Product("Fresh Carrots", "$2.99/lb", "carrotsgdp.jpg", "Sunny Farms", "3 lb", "2023-12-15");
        Product product3 = new Product("Organic Blackberries", "$5.49/pint", "blackberries.jpg", "Berry Bliss Farms", "1 pint", "2023-11-30");
        Product product4 = new Product("Organic Raspberries", "$5.99/pint", "rasp.jpg", "Berry Bliss Farms", "1 pint", "2023-11-25");
        Product product5 = new Product("Organic Kiwi", "$0.99 each", "kiwi.jpg", "Kiwi Valley", "1 each", "2023-12-10");
        Product product6 = new Product("Organic Lemons", "$1.99/lb", "lmns.png", "Citrus Farms", "2 lb", "2024-01-05");
        Product product7 = new Product("Organic Limes", "$1.79/lb", "lime.jpg", "Citrus Farms", "2 lb", "2024-01-10");

        // Push products to Firebase with unique keys
        String productId1 = productsRef.push().getKey();
        String productId2 = productsRef.push().getKey();
        String productId3 = productsRef.push().getKey();
        String productId4 = productsRef.push().getKey();
        String productId5 = productsRef.push().getKey();
        String productId6 = productsRef.push().getKey();
        String productId7 = productsRef.push().getKey();

        // Check for non-null product IDs and set the values
        if (productId1 != null) {
            productsRef.child(productId1).setValue(product1);
        }
        if (productId2 != null) {
            productsRef.child(productId2).setValue(product2);
        }
        if (productId3 != null) {
            productsRef.child(productId3).setValue(product3);
        }
        if (productId4 != null) {
            productsRef.child(productId4).setValue(product4);
        }
        if (productId5 != null) {
            productsRef.child(productId5).setValue(product5);
        }
        if (productId6 != null) {
            productsRef.child(productId6).setValue(product6);
        }
        if (productId7 != null) {
            productsRef.child(productId7).setValue(product7);
        }
    }

    // Method to retrieve product details by product ID
    public void getProductDetails(String productId, final ProductDetailsCallback callback) {
        productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                if (product != null) {
                    callback.onProductRetrieved(product);
                } else {
                    callback.onProductNotFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    // Callback interface for product retrieval
    public interface ProductDetailsCallback {
        void onProductRetrieved(Product product);
        void onProductNotFound();
        void onError(Exception e);
    }
}
