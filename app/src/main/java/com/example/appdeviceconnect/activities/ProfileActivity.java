package com.example.appdeviceconnect.activities;

import android.content.Intent;  // Import Intent for navigation
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.models.Customer;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileImage, tvName, tvEmail, tvAddress, tvPhoneNumber, tvCreatedDate;
    private String customerId; // Assume this comes from SharedPreferences or a previous activity
    private Button btnMyOrders; // Add a reference for the My Orders button
    private Button btnInactivateAccount; // Add a reference for the Inactivate Account button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        profileImage = findViewById(R.id.profileImage);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        btnMyOrders = findViewById(R.id.btnMyOrders); // Find the My Orders button
        btnInactivateAccount = findViewById(R.id.btnInactivateAccount); // Find the Inactivate Account button

        // Get customerId from SharedPreferences or Intent
        customerId = getCustomerId(); // Implement this method to fetch the ID of the logged-in user

        // Fetch the profile details from the API
        fetchProfileDetails(customerId);

        // Set an OnClickListener for the "My Orders" button to navigate to OrderActivity
        btnMyOrders.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrderActivity.class);
            intent.putExtra("customerId", customerId);  // Pass customerId to OrderActivity
            startActivity(intent);  // Start OrderActivity
        });

        // Set OnClickListener for the "Inactivate Account" button
        btnInactivateAccount.setOnClickListener(v -> {
            confirmDeactivation(); // Show confirmation before requesting deactivation
        });
    }

    private void fetchProfileDetails(String customerId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Customer> call = apiService.getCustomerById(customerId);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get customer details
                    Customer customer = response.body();

                    // Set the profile information
                    String firstName = customer.getFirstName();
                    String lastName = customer.getLastName();
                    tvName.setText("Name - " + firstName + " " + lastName);
                    tvEmail.setText("Email - " + customer.getEmail());
                    tvAddress.setText("Address - " + customer.getAddress());
                    tvPhoneNumber.setText("Phone Number - " + customer.getPhoneNumber());

                    // Format and set the account creation date
                    tvCreatedDate.setText("Account Created Date - " + formatDate(customer.getDateCreated()));

                    // Set the initials dynamically
                    String initials = getInitials(firstName, lastName);
                    profileImage.setText(initials);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to fetch profile details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to get the initials from first and last name
    private String getInitials(String firstName, String lastName) {
        return firstName.substring(0, 1).toUpperCase() + lastName.substring(0, 1).toUpperCase();
    }

    // Helper method to format the date
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString; // Return original string if parsing fails
        }
    }

    // Replace this method with your logic to retrieve the logged-in user's ID (from SharedPreferences or Intent)
    private String getCustomerId() {
        // Example from SharedPreferences
        return getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("customerId", null);
    }

    // Confirm deactivation before sending the request
    private void confirmDeactivation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deactivation")
                .setMessage("Are you sure you want to request deactivation of your account?")
                .setPositiveButton("Yes", (dialog, which) -> inactivateAccount(customerId))
                .setNegativeButton("No", null)
                .show();
    }

    // Call API to request account deactivation
    private void inactivateAccount(String customerId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.requestDeactivateAccount(customerId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Account deactivation request sent", Toast.LENGTH_SHORT).show();
                    btnInactivateAccount.setEnabled(false); // Disable the button after request
                    btnInactivateAccount.setText("Deactivation Requested");
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to request account deactivation", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
