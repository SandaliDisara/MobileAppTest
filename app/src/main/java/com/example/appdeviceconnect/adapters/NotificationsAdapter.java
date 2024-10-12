package com.example.appdeviceconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.models.Notification;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private Context context;
    private OnRemoveClickListener onRemoveClickListener;

    public NotificationsAdapter(Context context, List<Notification> notifications, OnRemoveClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.onRemoveClickListener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.body.setText(notification.getBody());

        // Format the date
        String formattedDate = formatDate(notification.getDateOfCreation());
        holder.date.setText(formattedDate);

        holder.removeButton.setOnClickListener(v -> {
            if (onRemoveClickListener != null) {
                onRemoveClickListener.onRemoveClick(notification.getNotificationId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0; // Avoid potential null pointer exceptions
    }

    private String formatDate(String dateString) {
        try {
            // Parse the incoming date string to a Date object
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            // Format the Date object to a user-friendly format
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString; // Fallback to the original string in case of error
        }
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, body, date;
        Button removeButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            body = itemView.findViewById(R.id.notificationBody);
            date = itemView.findViewById(R.id.notificationDate);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(String notificationId);
    }
}
