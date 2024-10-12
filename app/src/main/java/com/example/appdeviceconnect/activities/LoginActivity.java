package com.example.appdeviceconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.models.CustomerLoginDto;
import com.example.appdeviceconnect.models.Customer;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int POST_NOTIFICATIONS_REQUEST_CODE = 1; // Add constant for permission request

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;

    private TextView textCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textCreateAccount = findViewById(R.id.textCreateAccount);

        // Check for notification permission when the activity starts
        checkNotificationPermission();

        // Handle Login Button Click
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginActivity", "Login button pressed");
                loginUser(); // Proceed with login
            }
        });

        // Handle Create Account TextView click to navigate to RegisterActivity
        textCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RegisterActivity when "Create an account" is clicked
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to log in the user via API
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a CustomerLoginDto object to send to the server
        CustomerLoginDto loginDto = new CustomerLoginDto(email, password);

        // Call the API to authenticate the customer
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Customer> call = apiService.loginCustomer(loginDto);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Customer customer = response.body();

                    // Check if the account is active
                    if (customer.isActive()) {
                        // Store customerId in SharedPreferences
                        saveCustomerId(customer.getId());

                        // Fetch FCM token and send to the server
                        fetchAndSendFcmToken(customer.getId());

                        // Navigate to ProductListActivity
                        Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
                        startActivity(intent);
                        finish(); // Close the login activity
                    } else {
                        // Show a toast for inactive account
                        Toast.makeText(LoginActivity.this, "Account Inactive", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleLoginFailure(response);
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch FCM token and send it to the backend
    private void fetchAndSendFcmToken(String customerId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("LoginActivity", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("LoginActivity", "FCM Token: " + token);

                    // Send the FCM token to the backend
                    sendFcmTokenToServer(customerId, token);
                });
    }

    // Send FCM token to backend server
    // Send FCM token to backend server
    private void sendFcmTokenToServer(String customerId, String token) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.updateCustomerFcmToken(customerId, token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("LoginActivity", "FCM token updated successfully: " + response.code());
                } else {
                    Log.e("LoginActivity", "Failed to update FCM token. Code: " + response.code() + " Message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("LoginActivity", "Error: " + t.getMessage());
            }
        });
    }


    // Method to handle login failure
    private void handleLoginFailure(Response<Customer> response) {
        if (response.code() == 401) {
            try {
                String errorBody = response.errorBody().string();
                if (errorBody.contains("Account not activated")) {
                    Toast.makeText(LoginActivity.this, "Account Inactive", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save the customerId in SharedPreferences
    private void saveCustomerId(String customerId) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("customerId", customerId); // Store customerId
        editor.apply(); // Commit changes
    }

    // Method to check and request notification permissions
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request POST_NOTIFICATIONS permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        POST_NOTIFICATIONS_REQUEST_CODE);
            }
        }
    }

    // Callback for the result from requesting permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == POST_NOTIFICATIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
