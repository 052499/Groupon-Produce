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

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final Context context;

    // Constructor to initialize the adapter with context and product list
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for individual product items
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Bind the product data to the ViewHolder
        Product currentProduct = productList.get(position);
        holder.bind(currentProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder class to represent individual product items
    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
        }

        // Method to bind product data to the ViewHolder
        public void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText(product.getPrice());
            productImage.setImageResource(product.getImageResId());

            // Set click listener for product item
            itemView.setOnClickListener(view -> openProductDetailActivity(product));
        }

        // Navigate to ProductDetailActivity with product details
        private void openProductDetailActivity(Product product) {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productName", product.getName());
            intent.putExtra("productPrice", product.getPrice());
            intent.putExtra("farmName", product.getFarmName());
            intent.putExtra("quantity", product.getQuantity());
            intent.putExtra("expiryDate", product.getExpiryDate());
            intent.putExtra("imageResId", product.getImageResId());
            context.startActivity(intent);
        }
    }
}
