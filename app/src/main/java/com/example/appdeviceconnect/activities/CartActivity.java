package com.example.appdeviceconnect.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.adapters.CartAdapter;
import com.example.appdeviceconnect.models.CartItem;
import com.example.appdeviceconnect.models.Vendor;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView subTotalText, vatText, netTotalText;
    private Button checkoutButton;
    private String customerId;
    private double netTotal = 0.0; // Track net total
    private List<CartItem> cartItems; // Store cart items

    // Fixed VAT percentage (e.g., 5%)
    private final double VAT_PERCENTAGE = 0.05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Retrieve customerId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        customerId = sharedPreferences.getString("customerId", null); // Retrieve stored customerId

        if (customerId == null) {
            Toast.makeText(this, "Customer not logged in. Please log in.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if customerId is not found
            return;
        }

        // Initialize UI components
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        subTotalText = findViewById(R.id.subTotalText);
        vatText = findViewById(R.id.vatText);
        netTotalText = findViewById(R.id.netTotalText);
        checkoutButton = findViewById(R.id.checkoutButton);

        // Fetch cart items
        fetchCartItems();

        // Handle checkout button click
        checkoutButton.setOnClickListener(v -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                for (CartItem item : cartItems) {
                    if (item.getVendorId() == null || item.getVendorId().isEmpty()) {
                        Toast.makeText(CartActivity.this, "Error: Vendor information missing for some items", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems)); // Pass cart items
                intent.putExtra("totalPrice", netTotal); // Pass net total to checkout screen
                intent.putExtra("customerId", customerId); // Pass customer ID
                startActivity(intent);
            } else {
                Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCartItems() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<CartItem>> call = apiService.getCartByCustomerId(customerId);

        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems = response.body();

                    // For each cart item, fetch the vendor name using vendorId
                    for (CartItem item : cartItems) {
                        if (item.getVendorId() != null && !item.getVendorId().isEmpty()) {
                            fetchVendorName(item);
                        } else {
                            item.setVendorName("Unknown Vendor");
                        }
                    }

                    // Pass the customerId to the adapter and set a callback to update the summary
                    cartAdapter = new CartAdapter(cartItems, CartActivity.this, customerId, () -> calculateSummary(cartItems));
                    cartRecyclerView.setAdapter(cartAdapter);

                    // Calculate the summary (subtotal, VAT, net total)
                    calculateSummary(cartItems);
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e("CartActivity", "Error fetching cart items", t);
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchVendorName(CartItem cartItem) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String vendorId = cartItem.getVendorId();

        if (vendorId == null || vendorId.isEmpty()) {
            cartItem.setVendorName("Unknown Vendor");
            cartAdapter.notifyDataSetChanged();
            return;
        }

        Call<Vendor> call = apiService.getVendorById(vendorId);

        call.enqueue(new Callback<Vendor>() {
            @Override
            public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Vendor vendor = response.body();
                    cartItem.setVendorName(vendor.getName()); // Set vendor name in the CartItem
                    Log.d("CartActivity", "Vendor name set: " + vendor.getName());
                    cartAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                } else {
                    Log.e("CartActivity", "Failed to load vendor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Vendor> call, Throwable t) {
                Log.e("CartActivity", "Error fetching vendor", t);
            }
        });
    }


    // Method to calculate subtotal, VAT, and net total
    private void calculateSummary(List<CartItem> cartItems) {
        double subTotal = 0;

        // Calculate subtotal by summing up the total for each cart item
        for (CartItem item : cartItems) {
            subTotal += item.getPrice() * item.getQuantity();
        }

        // Calculate VAT
        double vat = subTotal * VAT_PERCENTAGE;

        // Calculate net total
        netTotal = subTotal + vat;

        // Update the UI
        subTotalText.setText(String.format("Sub Total: Rs.%.2f", subTotal));
        vatText.setText(String.format("VAT: Rs.%.2f", vat));
        netTotalText.setText(String.format("Net Total: Rs.%.2f", netTotal));
    }
}
