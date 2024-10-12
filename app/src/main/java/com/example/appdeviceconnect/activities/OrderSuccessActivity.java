package com.example.appdeviceconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdeviceconnect.R;

public class OrderSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // Initialize the TextView and Button
        TextView successMessage = findViewById(R.id.successMessage);
        Button goBackButton = findViewById(R.id.goBackButton);

        // Retrieve order details from the intent (if needed)
        String orderId = getIntent().getStringExtra("ORDER_ID"); // Example key, replace with your key
        String totalPrice = getIntent().getStringExtra("TOTAL_PRICE"); // Example key, replace with your key

        // Set the success message dynamically
        if (orderId != null && totalPrice != null) {
            successMessage.setText("Your order (ID: " + orderId + ") has been placed successfully!\nTotal: Rs. " + totalPrice);
        } else {
            successMessage.setText("Your order has been placed successfully!");
        }

        // Set click listener for the "Continue Shopping" button
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the main activity or the desired activity
                Intent intent = new Intent(OrderSuccessActivity.this, ProductListActivity.class); // Replace with your main activity
                startActivity(intent);
                finish(); // Optional: Finish this activity if you don't want to return to it
            }
        });
    }
}
