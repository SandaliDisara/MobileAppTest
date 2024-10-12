package com.example.appdeviceconnect.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.models.Notification; // Import Notification model
import com.example.appdeviceconnect.models.Order;
import com.example.appdeviceconnect.models.ProductOrder; // Import ProductOrder
import com.example.appdeviceconnect.network.ApiService; // Import your ApiService
import com.example.appdeviceconnect.network.RetrofitClient; // Import Retrofit client

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context; // Context for showing dialogs and toasts
    private List<String> cancelRequestsSent; // List to track orders with cancel requests

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context; // Initialize context
        this.orderList = orderList;
        this.cancelRequestsSent = new ArrayList<>(); // Initialize the cancel request list
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Extract the date from the orderDate string
        String formattedDate = order.getOrderDate().substring(0, 10); // Get the first 10 characters

        // Set order date and status
        holder.orderDate.setText(formattedDate);
        holder.orderStatus.setText(order.getOrderStatus());

        // Set total price with two decimal places
        holder.orderTotal.setText(String.format("Total Price: Rs. %.2f", order.getTotalPrice()));

        // Retrieve and display the product list with quantities and statuses
        StringBuilder productList = new StringBuilder();
        for (ProductOrder product : order.getProducts()) {
            productList.append(product.getProductName())
                    .append(" - ").append(product.getQuantity())
                    .append("\n")
                    .append(" Product Status: ").append(product.getProductStatus())
                    .append("\n");
        }

        holder.orderProducts.setText(productList.toString());

        // Show or hide the Cancel Order button based on the order status
        if (!order.getOrderStatus().equalsIgnoreCase("Delivered")) {
            // Check if the cancel request has been sent
            if (cancelRequestsSent.contains(order.getId())) {
                holder.cancelOrderButton.setText("Cancel Request Sent"); // Update button text
                holder.cancelOrderButton.setEnabled(false); // Disable the button
            } else {
                holder.cancelOrderButton.setText("Cancel Order"); // Reset button text
                holder.cancelOrderButton.setEnabled(true); // Enable the button
            }
            holder.cancelOrderButton.setVisibility(View.VISIBLE);
        } else {
            holder.cancelOrderButton.setVisibility(View.GONE);
        }

        // Set an OnClickListener for the Cancel Order button
        holder.cancelOrderButton.setOnClickListener(v -> {
            showCancellationDialog(order, holder); // Show dialog for cancellation and pass the ViewHolder
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate, orderTotal, orderProducts, orderStatus;
        Button cancelOrderButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderProducts = itemView.findViewById(R.id.orderProducts);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            cancelOrderButton = itemView.findViewById(R.id.cancelOrderButton);
        }
    }

    private void showCancellationDialog(Order order, OrderViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cancel Order");

        // Set up the input
        final EditText input = new EditText(context);
        input.setHint("Reason for cancellation");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String reason = input.getText().toString();
            if (!reason.isEmpty()) {
                createNotification(order.getId(), reason, holder); // Call method to create notification
            } else {
                Toast.makeText(context, "Please provide a reason", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createNotification(String orderId, String reason, OrderViewHolder holder) {
        // Retrieve customer ID from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE); // Use "AppPrefs"
        String customerId = sharedPreferences.getString("customerId", null); // Default to null if not found

        // Check if customerId is null
        if (customerId == null) {
            Toast.makeText(context, "Customer ID not found. Cannot create notification.", Toast.LENGTH_SHORT).show();
            return; // Early exit if customerId is null
        }

        // Create notification object
        Notification notification = new Notification();
        notification.setType("BackOffice");
        notification.setTitle("Order Cancelling");
        notification.setBody(reason);
        notification.setOrderId(orderId);
        notification.setSenderId(customerId); // Set the customer ID from SharedPreferences
        notification.setDateOfCreation(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(new Date()));

        // Create an instance of Retrofit
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // Make the API call
        Call<Notification> call = apiService.createNotification(notification);
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Notification created successfully!", Toast.LENGTH_SHORT).show();

                    // Add the order ID to the list of orders with cancel requests
                    cancelRequestsSent.add(orderId);

                    // Update the button text and disable the button
                    holder.cancelOrderButton.setText("Cancel Request Sent");
                    holder.cancelOrderButton.setEnabled(false);
                } else {
                    Toast.makeText(context, "Failed to create notification", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

