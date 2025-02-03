package com.example.grouponproduceapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice());

        // Load image (Firebase URL or Local Resource)
        if (product.getImageUrl() != null) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.srch)
                    .error(R.drawable.srch)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(product.getImageResId());
        }

        // Click listener to open product details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productName", product.getName());
            intent.putExtra("productPrice", product.getPrice());
            intent.putExtra("farmName", product.getFarmName());
            intent.putExtra("quantity", product.getQuantity());
            intent.putExtra("expiryDate", product.getExpiryDate());

            if (product.getImageUrl() != null) {
                intent.putExtra("imageUrl", product.getImageUrl());
            } else {
                intent.putExtra("imageResId", product.getImageResId());
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }
}
