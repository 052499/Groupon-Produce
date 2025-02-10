package com.example.grouponproduceapp;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product newProduct) {
        for (Product item : cartItems) {
            if (item.getName().equals(newProduct.getName())) {
                int updatedQuantity = item.getQuantityAsInt() + newProduct.getQuantityAsInt();
                item.setQuantity(updatedQuantity); // Ensure it remains a string
                return;
            }
        }
        cartItems.add(newProduct);
    }

    public void removeFromCart(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<Product> getCartItems() {
        return new ArrayList<>(cartItems);
    }
}
