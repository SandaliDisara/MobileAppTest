package com.example.appdeviceconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Import Glide
import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.activities.ProductDetailsActivity;
import com.example.appdeviceconnect.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("Rs. %.2f", product.getPrice()));

        // Check if imageURL is null or empty
        if (product.getImageURL() == null || product.getImageURL().isEmpty()) {
            // Set a solid background color if there's no image
            holder.productImage.setImageResource(0); // Clear the image
            holder.productImage.setBackgroundColor(context.getResources().getColor(R.color.grey)); // Set to grey or any color
        } else {
            // Load the image from imageURL using Glide
            Glide.with(context)
                    .load(product.getImageURL())
                    .into(holder.productImage);
            holder.productImage.setBackgroundColor(0); // Clear the background color
        }

        // Debug vendorId
        Log.d("ProductAdapter", "Vendor ID: " + product.getVendorId());

        // Set up click listener to navigate to ProductDetailsActivity with product details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_name", product.getName());
            intent.putExtra("product_description", product.getDescription());
            intent.putExtra("product_price", product.getPrice());
            intent.putExtra("vendor_id", product.getVendorId());
            intent.putExtra("image_url", product.getImageURL()); // Ensure vendorId is passed
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged(); // Notify the adapter to refresh the view
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}
