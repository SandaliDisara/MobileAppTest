package com.example.appdeviceconnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Import Glide
import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.models.CartItem;
import com.example.appdeviceconnect.models.Vendor;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView productName, productDescription, productPrice, vendorNameTextView;
    private Button addToCartButton;
    private ImageView productImage; // Declare ImageView for the product image
    private boolean isItemAddedToCart = false; // Track if the item was added to cart
    private String customerId; // Will be fetched dynamically
    private String vendorId; // Store the vendor ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize views
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        vendorNameTextView = findViewById(R.id.vendorName);
        addToCartButton = findViewById(R.id.addToCartButton);
        productImage = findViewById(R.id.productImage); // Initialize the ImageView

        // Retrieve customerId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        customerId = sharedPreferences.getString("customerId", null);

        if (customerId == null) {
            Toast.makeText(this, "Customer ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            addToCartButton.setEnabled(false); // Disable Add to Cart button
            return;
        }

        // Get the data passed from ProductAdapter
        String name = getIntent().getStringExtra("product_name");
        String description = getIntent().getStringExtra("product_description");
        double price = getIntent().getDoubleExtra("product_price", 0);
        String productId = getIntent().getStringExtra("product_id");
        vendorId = getIntent().getStringExtra("vendor_id");
        String imageUrl = getIntent().getStringExtra("image_url"); // Fetch the image URL

        // Log product and vendor details for debugging
        Log.d("ProductDetails", "Product ID: " + productId + ", Name: " + name + ", Price: " + price + ", Vendor ID: " + vendorId);

        if (productId == null) {
            Toast.makeText(this, "Error: Product ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the product data to the TextViews
        productName.setText(name);
        productDescription.setText(description);
        productPrice.setText("Price: Rs." + price);

        // Load the product image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(productImage);
        } else {
            // Set a solid color as background if there is no image
            productImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray)); // Gray background for no image
            productImage.setImageResource(0); // Clear the image if there is none
        }

        // Fetch and display the vendor name
        if (vendorId != null) {
            fetchVendorName(vendorId);
        }

        // Navigate to VendorDetailsActivity when vendor name is clicked
        vendorNameTextView.setOnClickListener(v -> {
            if (vendorId != null) {
                // Start VendorDetailsActivity and pass the vendor ID
                Intent intent = new Intent(ProductDetailsActivity.this, VendorDetailsActivity.class);
                intent.putExtra("vendor_id", vendorId);
                startActivity(intent);
            } else {
                Toast.makeText(ProductDetailsActivity.this, "Vendor details unavailable", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Add to Cart button click
        addToCartButton.setOnClickListener(v -> {
            if (!isItemAddedToCart) {
                addToCart(customerId, productId, vendorId, name, 1, price); // Add vendorId when adding to cart
            }
        });
    }

    private void fetchVendorName(String vendorId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Make an API call to get the vendor details
        Call<Vendor> call = apiService.getVendorById(vendorId);
        call.enqueue(new Callback<Vendor>() {
            @Override
            public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Vendor vendor = response.body();
                    vendorNameTextView.setText("Vendor: " + vendor.getName());
                } else {
                    Log.e("VendorError", "Failed to fetch vendor details: response code " + response.code());
                    Toast.makeText(ProductDetailsActivity.this, "Failed to load vendor details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Vendor> call, Throwable t) {
                Log.e("VendorError", "API call failure: " + t.getMessage());
                Toast.makeText(ProductDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart(String customerId, String productId, String vendorId, String productName, int quantity, double price) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Log the cartItem and customerId details to debug
        Log.d("CartItemDetails", "Customer ID: " + customerId);
        Log.d("CartItemDetails", "Product ID: " + productId + ", Vendor ID: " + vendorId + ", Product Name: " + productName + ", Quantity: " + quantity + ", Price: " + price);

        // Create CartItem object with vendorId
        CartItem cartItem = new CartItem(productId, productName, vendorId, quantity, price);

        // Make the API call to add the product to the cart
        Call<Void> call = apiService.addToCart(customerId, cartItem);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailsActivity.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();
                    isItemAddedToCart = true;
                    addToCartButton.setText("Item added to cart");
                    addToCartButton.setEnabled(false);
                } else {
                    Log.e("CartError", "Failed to add to cart: response code " + response.code());
                    try {
                        Log.e("CartError", "Response error: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ProductDetailsActivity.this, "Failed to add to cart: response code " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CartError", "API call failure: " + t.getMessage());
                Toast.makeText(ProductDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
