package com.example.appdeviceconnect.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.adapters.ProductAdapter;
import com.example.appdeviceconnect.models.Product;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Two items per row

        // Fetch Products from the API
        fetchProducts();
    }

    private void fetchProducts() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Product>> call = apiService.getProducts();

        // Show a loading message to the user (optional)
        Toast.makeText(this, "Loading products...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productList = response.body();

                    // Check if the list is empty
                    if (productList.isEmpty()) {
                        Toast.makeText(ProductListActivity.this, "No products available", Toast.LENGTH_SHORT).show();
                    } else {
                        // Initialize and set the adapter with fetched products
                        productAdapter = new ProductAdapter(productList, ProductListActivity.this);
                        recyclerView.setAdapter(productAdapter);
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "API call failed: " + t.getMessage());
            }
        });
    }
}
