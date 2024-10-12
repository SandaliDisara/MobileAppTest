package com.example.appdeviceconnect.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.adapters.NotificationsAdapter;
import com.example.appdeviceconnect.models.Notification;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<Notification> notifications;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        notifications = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(this, notifications, this::removeNotification);
        notificationsRecyclerView.setAdapter(notificationsAdapter);

        // Fetch notifications when the activity is created
        fetchNotifications();
    }

    private void fetchNotifications() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Notification>> call = apiService.getNotifications(); // Fetch notifications from the API

        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notifications.clear(); // Clear the list before adding new items
                    notifications.addAll(response.body()); // Add the fetched notifications to the list
                    notificationsAdapter.notifyDataSetChanged(); // Notify the adapter of the data change
                } else {
                    Toast.makeText(NotificationsActivity.this, "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeNotification(String notificationId) {
        // Implement logic to remove the notification from the list and notify the adapter
        Toast.makeText(this, "Removing notification ID: " + notificationId, Toast.LENGTH_SHORT).show();
        notifications.removeIf(notification -> notification.getNotificationId().equals(notificationId));
        notificationsAdapter.notifyDataSetChanged();

        // Optionally, you can implement the API call to delete the notification here
        // Call the API to delete the notification and handle the response accordingly
    }
}
