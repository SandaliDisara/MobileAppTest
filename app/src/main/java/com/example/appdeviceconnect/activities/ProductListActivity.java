package com.example.appdeviceconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.adapters.CategoryAdapter;
import com.example.appdeviceconnect.adapters.ProductAdapter;
import com.example.appdeviceconnect.models.Product;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private RecyclerView categoriesRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private List<Product> allProducts;
    private List<Product> activeProducts; // Store only active products
    private View noProductsView; // To show when no products are available

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // View that shows when there are no products
        noProductsView = findViewById(R.id.noProductsView);

        List<String> categories = Arrays.asList("All", "Mobile", "Laptop", "Tv", "Cameras", "Accessories");
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, this, this);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // Navigate to Home (in this case, stay in Product List)
                return true;
            } else if (itemId == R.id.navigation_cart) {
                // Navigate to Cart Activity
                startActivity(new Intent(ProductListActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.navigation_notifications) {
                // Navigate to Notifications Activity
                startActivity(new Intent(ProductListActivity.this, NotificationsActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Navigate to Profile Activity
                startActivity(new Intent(ProductListActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });

        fetchProducts();
    }

    private void fetchProducts() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Product>> call = apiService.getProducts();

        Toast.makeText(this, "Loading products...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();

                    // Filter out products that are inactive
                    activeProducts = new ArrayList<>();
                    for (Product product : allProducts) {
                        if (product.isActive()) {
                            activeProducts.add(product);
                        }
                    }

                    // Set the adapter with only active products
                    showProducts(activeProducts);
                } else {
                    Toast.makeText(ProductListActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCategoryClick(String category) {
        if (category.equals("All")) {
            // Show all active products
            showProducts(activeProducts);
        } else {
            // Filter the active products by category
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : activeProducts) {
                if (product.getCategory().equalsIgnoreCase(category)) {
                    filteredProducts.add(product);
                }
            }
            showProducts(filteredProducts);
        }
    }

    // Helper method to display the products or the "No products available" message
    private void showProducts(List<Product> products) {
        if (products.isEmpty()) {
            // Show the "No products available" message
            recyclerView.setVisibility(View.GONE);
            noProductsView.setVisibility(View.VISIBLE);
        } else {
            // Show the products in the RecyclerView
            noProductsView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            productAdapter = new ProductAdapter(products, this);
            recyclerView.setAdapter(productAdapter);
        }
    }
}


