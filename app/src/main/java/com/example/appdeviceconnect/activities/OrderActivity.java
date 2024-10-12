package com.example.appdeviceconnect.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.adapters.OrderAdapter;
import com.example.appdeviceconnect.models.Order;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private String customerId; // To store the customer ID
    private List<Order> allOrders; // Store all orders for filtering
    private RadioGroup ordersToggleGroup; // Toggle group for filtering orders

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize RecyclerView
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get customerId from Intent extras
        customerId = getIntent().getStringExtra("customerId");

        // Initialize RadioGroup
        ordersToggleGroup = findViewById(R.id.ordersToggleGroup);
        ordersToggleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (allOrders != null) {
                filterOrders(checkedId);
            }
        });

        if (customerId != null) {
            // Fetch orders for the customer
            fetchOrders(customerId);
        } else {
            Toast.makeText(this, "Customer ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchOrders(String customerId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Order>> call = apiService.getOrdersByCustomerId(customerId);

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allOrders = response.body(); // Store all orders
                    if (allOrders.isEmpty()) {
                        Toast.makeText(OrderActivity.this, "No orders found", Toast.LENGTH_SHORT).show();
                    } else {
                        // Set up adapter with the fetched orders
                        orderAdapter = new OrderAdapter(OrderActivity.this, allOrders); // Pass context here
                        ordersRecyclerView.setAdapter(orderAdapter);
                    }
                } else {
                    Log.e("OrderActivity", "Response not successful: " + response.code());
                    Toast.makeText(OrderActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("OrderActivity", "Error fetching orders: " + t.getMessage());
                Toast.makeText(OrderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOrders(int checkedId) {
        List<Order> filteredOrders = new ArrayList<>();

        // Check which RadioButton is selected
        if (checkedId == R.id.radioOrdersInPlace) {
            // Filter for "Orders in Place"
            for (Order order : allOrders) {
                // Change condition to check for not delivered orders
                if (!order.getOrderStatus().equalsIgnoreCase("Delivered")) {
                    filteredOrders.add(order);
                }
            }
        } else if (checkedId == R.id.radioOrderHistory) {
            // Filter for "Order History"
            for (Order order : allOrders) {
                // Check for delivered orders
                if (order.getOrderStatus().equalsIgnoreCase("Delivered")) {
                    filteredOrders.add(order);
                }
            }
        }

        // Update RecyclerView with filtered orders
        orderAdapter = new OrderAdapter(OrderActivity.this, filteredOrders);
        ordersRecyclerView.setAdapter(orderAdapter);
    }
}
