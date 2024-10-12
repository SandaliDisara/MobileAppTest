package com.example.appdeviceconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdeviceconnect.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<String> categoryList;
    private Context context;
    private OnCategoryClickListener listener;
    private String selectedCategory; // Variable to track selected category

    public CategoryAdapter(List<String> categoryList, Context context, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.context = context;
        this.listener = listener;
        this.selectedCategory = ""; // Initialize with an empty string or a default category
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false); // Ensure this layout file exists
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categoryList.get(position);
        holder.categoryName.setText(category);

        // Change the background and text color based on selection
        if (category.equals(selectedCategory)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.purple_500)); // Dark purple color
            holder.categoryName.setTextColor(context.getResources().getColor(android.R.color.white)); // White text
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent)); // Default background
            holder.categoryName.setTextColor(context.getResources().getColor(android.R.color.black)); // Default text color
        }

        // Set up click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                selectedCategory = category; // Update selected category
                listener.onCategoryClick(category); // Notify the click event
                notifyDataSetChanged(); // Refresh the view to update styles
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryText); // Ensure this ID matches your XML layout
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }
}
