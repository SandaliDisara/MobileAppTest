package com.example.appdeviceconnect.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.adapters.CommentAdapter;
import com.example.appdeviceconnect.models.Comment;
import com.example.appdeviceconnect.models.Vendor;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendorDetailsActivity extends AppCompatActivity {

    private TextView vendorName, vendorDescription, vendorRating;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private Button addCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);

        // Initialize views
        vendorName = findViewById(R.id.vendorName);
        vendorDescription = findViewById(R.id.vendorDescription);
        vendorRating = findViewById(R.id.vendorRating);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        addCommentButton = findViewById(R.id.addCommentButton);

        // Get the vendor ID passed from the previous activity
        String vendorId = getIntent().getStringExtra("vendor_id");

        // Check if vendorId is valid
        if (vendorId == null || vendorId.isEmpty()) {
            Toast.makeText(this, "Vendor ID is missing", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if vendorId is missing
            return;
        }

        // Set up the RecyclerView
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch vendor details from API
        fetchVendorDetails(vendorId);

        // Handle "Add Comment" button click
        addCommentButton.setOnClickListener(v -> showCommentDialog(vendorId));
    }

    // Method to show the dialog to add a comment
    private void showCommentDialog(String vendorId) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_add_comment, null);

        EditText commentEditText = view.findViewById(R.id.commentEditText);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        Button postButton = view.findViewById(R.id.postButton);

        // Create and show the dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Add Comment")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();

        // Handle post comment button
        postButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString().trim();
            int rating = (int) ratingBar.getRating();

            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(VendorDetailsActivity.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating == 0) {
                Toast.makeText(VendorDetailsActivity.this, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch customerId from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String customerId = sharedPreferences.getString("customerId", null);
            String customerName = sharedPreferences.getString("customerName", "Customer");

            if (customerId == null) {
                Toast.makeText(VendorDetailsActivity.this, "Customer ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Post the comment
            postComment(vendorId, customerId, customerName, commentText, rating);

            dialog.dismiss();
        });

        dialog.show();
    }

    // Method to fetch vendor details
    private void fetchVendorDetails(String vendorId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Vendor> call = apiService.getVendorById(vendorId);

        call.enqueue(new Callback<Vendor>() {
            @Override
            public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Vendor vendor = response.body();

                    // Display vendor details
                    vendorName.setText(vendor.getName());
                    vendorDescription.setText(vendor.getDescription());
                    vendorRating.setText("Rating: " + vendor.getAverageRanking());

                    // Ensure comments are not null or empty
                    if (vendor.getComments() != null && !vendor.getComments().isEmpty()) {
                        commentAdapter = new CommentAdapter(vendor.getComments());
                        commentsRecyclerView.setAdapter(commentAdapter);
                    } else {
                        Toast.makeText(VendorDetailsActivity.this, "No comments available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("VendorDetails", "Failed to fetch vendor details: " + response.code());
                    Toast.makeText(VendorDetailsActivity.this, "Failed to load vendor details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Vendor> call, Throwable t) {
                Log.e("VendorDetails", "API call failure: " + t.getMessage());
                Toast.makeText(VendorDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to post a comment
    private void postComment(String vendorId, String customerId, String customerName, String commentText, int rating) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Comment comment = new Comment(customerId, customerName, commentText, rating);

        Call<Void> call = apiService.addCommentToVendor(vendorId, comment);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VendorDetailsActivity.this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                    fetchVendorDetails(vendorId); // Refresh vendor details to show the new comment
                } else {
                    Toast.makeText(VendorDetailsActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(VendorDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
