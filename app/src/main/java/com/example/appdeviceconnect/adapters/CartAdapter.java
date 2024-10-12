package com.example.appdeviceconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdeviceconnect.R;
import com.example.appdeviceconnect.models.CartItem;
import com.example.appdeviceconnect.network.ApiService;
import com.example.appdeviceconnect.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private String customerId;
    private Runnable updateSummaryCallback; // Callback to trigger subtotal and total price update

    public CartAdapter(List<CartItem> cartItems, Context context, String customerId, Runnable updateSummaryCallback) {
        this.cartItems = cartItems;
        this.context = context;
        this.customerId = customerId;
        this.updateSummaryCallback = updateSummaryCallback; // Initialize callback for subtotal update
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.productName.setText(cartItem.getProductName());

        // Calculate total price based on quantity and price per item
        double totalPrice = cartItem.getQuantity() * cartItem.getPrice();
        holder.productPrice.setText("Total Price: Rs." + String.format("%.2f", totalPrice));
        holder.productQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // Increase quantity
        holder.increaseQuantity.setOnClickListener(v -> {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            updateCartItem(cartItem, holder);  // Update cart in the backend and refresh UI
        });

        // Decrease quantity
        holder.decreaseQuantity.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                updateCartItem(cartItem, holder);  // Update cart in the backend and refresh UI
            } else {
                // Optionally, you can remove the item from the cart if quantity is 1
                removeCartItem(cartItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // ViewHolder class
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;
        Button increaseQuantity, decreaseQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.cartProductName);
            productPrice = itemView.findViewById(R.id.cartProductPrice);
            productQuantity = itemView.findViewById(R.id.cartProductQuantity);
            increaseQuantity = itemView.findViewById(R.id.increaseQuantity);
            decreaseQuantity = itemView.findViewById(R.id.decreaseQuantity);
        }
    }

    // Method to update the cart item quantity in the backend
    private void updateCartItem(CartItem cartItem, CartViewHolder holder) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.updateCartItem(customerId, cartItem);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Calculate and update the total price based on the new quantity
                    double updatedPrice = cartItem.getQuantity() * cartItem.getPrice();
                    holder.productPrice.setText("Total Price: Rs." + String.format("%.2f", updatedPrice));
                    holder.productQuantity.setText(String.valueOf(cartItem.getQuantity()));

                    // Call the callback to update the summary (subtotal, VAT, etc.)
                    if (updateSummaryCallback != null) {
                        updateSummaryCallback.run();
                    }
                } else {
                    Toast.makeText(context, "Failed to update cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error updating cart: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to remove the cart item if quantity is reduced to 0
    private void removeCartItem(CartItem cartItem, int position) {
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());

        // Optionally call backend to remove the item from the cart
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<Void> call = apiService.removeCartItem(customerId, cartItem.getProductId());  // Assuming removeCartItem exists

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Failed to remove item from cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Call the callback to update the summary when an item is removed
                    if (updateSummaryCallback != null) {
                        updateSummaryCallback.run();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error removing item from cart: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
