package com.example.grouponproduceapp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
    }

    public void addSampleProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("Organic Apples", "$3.99/lb", "applesgdp", "Green Orchard Farm", "5 lb", "2023-12-01"));
        products.add(new Product("Organic Bananas", "$1.29/lb", "bananagdp", "Tropical Farms", "1 lb", "2023-11-20"));
        products.add(new Product("Organic Carrots", "$2.99/lb", "carrotsgdp", "Rooted Farm", "3 lb", "2023-12-15"));
        products.add(new Product("Organic Blackberries", "$5.49/pint", "blackberries", "Berry Bliss Farms", "1 pint", "2023-11-30"));
        products.add(new Product("Organic Raspberries", "$5.99/pint", "rasp", "Berry Bliss Farms", "1 pint", "2023-11-25"));
        products.add(new Product("Organic Kiwi", "$0.99 each", "kiwi", "Kiwi Valley", "1 each", "2023-12-10"));
        products.add(new Product("Organic Lemons", "$1.99/lb", "lmns", "Citrus Farms", "2 lb", "2024-01-05"));
        products.add(new Product("Organic Limes", "$1.79/lb", "lime", "Citrus Farms", "2 lb", "2024-01-10"));

        for (Product product : products) {
            databaseReference.push().setValue(product);
        }
    }
}
