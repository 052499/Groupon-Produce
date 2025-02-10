package com.example.grouponproduceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final ArrayList<Product> cartItems;
    private final Context context;

    public CartAdapter(Context context, ArrayList<Product> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product item = cartItems.get(position);
        holder.productName.setText(item.getName());
        holder.productPrice.setText("$" + item.getPrice());
        holder.productQuantity.setText("Quantity: " + item.getQuantity());

        // Load Image from Drawable using Image Name
        int imageResId = context.getResources().getIdentifier(item.getImageName(), "drawable", context.getPackageName());
        holder.productImage.setImageResource(imageResId);

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (position >= 0 && position < cartItems.size()) {
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        ImageView productImage;
        Button deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productImage = itemView.findViewById(R.id.product_image);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}

