package com.example.appdeviceconnect.network;

import com.example.appdeviceconnect.models.CartItem;
import com.example.appdeviceconnect.models.Comment;
import com.example.appdeviceconnect.models.Notification;
import com.example.appdeviceconnect.models.Order;
import com.example.appdeviceconnect.models.Product;
import com.example.appdeviceconnect.models.CustomerRegisterDto;
import com.example.appdeviceconnect.models.Customer;
import com.example.appdeviceconnect.models.CustomerLoginDto;
import com.example.appdeviceconnect.models.Vendor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Get the list of products
    @GET("/api/products")
    Call<List<Product>> getProducts();

    // Get a specific product by id
    @GET("/api/Products/{id}")
    Call<Product> getProductById(@Path("id") String productId);

    // Add an item to the cart
    @POST("/api/Cart/{customerId}/add")
    Call<Void> addToCart(@Path("customerId") String customerId, @Body CartItem cartItem);

    @PUT("/api/Cart/{customerId}/update")
    Call<Void> updateCartItem(@Path("customerId") String customerId, @Body CartItem cartItem);

    @DELETE("/api/Cart/{customerId}/delete/{productId}")
    Call<Void> removeCartItem(@Path("customerId") String customerId, @Path("productId") String productId);

    @POST("/api/Order")
    Call<Void> createOrder(@Body Order order);

    // Get cart items by customer ID
    @GET("/api/Cart/{customerId}")
    Call<List<CartItem>> getCartByCustomerId(@Path("customerId") String customerId);

    // Get vendor details by vendor ID
    @GET("/api/Vendor/{id}")
    Call<Vendor> getVendorById(@Path("id") String vendorId);

    @PUT("/api/Vendor/{id}/comment")
    Call<Void> addCommentToVendor(@Path("id") String vendorId, @Body Comment comment);

    @GET("/api/Customer/{id}")
    Call<Customer> getCustomerById(@Path("id") String customerId);

    @GET("/api/Order/customer/{customerId}")
    Call<List<Order>> getOrdersByCustomerId(@Path("customerId") String customerId);

    @POST("/api/Notification")
    Call<Notification> createNotification(@Body Notification notification);

    @GET("/api/Notification")
    Call<List<Notification>> getNotifications(); // New API endpoint for notifications

    @POST("/api/Fcm/{customerId}/fcm-token")
    Call<Void> updateCustomerFcmToken(@Path("customerId") String customerId, @Body String fcmToken);

    @PUT("/api/Customer/request-deactivate/{id}")
    Call<Void> requestDeactivateAccount(@Path("id") String customerId);

    // Register a new customer
    @POST("/api/Customer/register")
    Call<Void> registerCustomer(@Body CustomerRegisterDto customerDto);

    // Customer login
    @POST("/api/Customer/login")
    Call<Customer> loginCustomer(@Body CustomerLoginDto customerLoginDto);
}
