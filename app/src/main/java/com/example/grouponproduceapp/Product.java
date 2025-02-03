package com.example.grouponproduceapp;

public class Product {
    private String key;       // Firebase key
    private String name;
    private String price;
    private String imageUrl;  // Firebase storage image URL
    private int imageResId;   // Local drawable image resource ID
    private String farmName;
    private String quantity;
    private String expiryDate;

    // Default constructor required for Firebase
    public Product() {
    }

    // Constructor for Firebase (when loading from the database)
    public Product(String key, String name, String price, String imageUrl, String farmName, String quantity, String expiryDate) {
        this.key = key;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.imageResId = 0; // Not used in Firebase products
        this.farmName = farmName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    // Constructor for local sample data (using drawable images)
    public Product(String key, String name, String price, int imageResId, String farmName, String quantity, String expiryDate) {
        this.key = key;
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.imageUrl = null; // Not used for local products
        this.farmName = farmName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    // Getters
    public String getKey() { return key; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getImageResId() { return imageResId; }
    public String getFarmName() { return farmName; }
    public String getQuantity() { return quantity; }
    public String getExpiryDate() { return expiryDate; }

    // Setters (if needed for Firebase updates)
    public void setKey(String key) { this.key = key; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setFarmName(String farmName) { this.farmName = farmName; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
