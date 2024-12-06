package com.example.grouponproduceapp;

public class product {
    private String name;
    private String price;
    private int imageResId;
    private String farmName;
    private String quantity;
    private String expiryDate;

    // Constructor
    public product(String name, String price, int imageResId, String farmName, String quantity, String expiryDate) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.farmName = farmName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    // Getters
    public String getName() { return name; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getFarmName() { return farmName; }
    public String getQuantity() { return quantity; }
    public String getExpiryDate() { return expiryDate; }

    // Optional: ToString method to help with debugging
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", imageResId=" + imageResId +
                ", farmName='" + farmName + '\'' +
                ", quantity='" + quantity + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                '}';
    }
}
