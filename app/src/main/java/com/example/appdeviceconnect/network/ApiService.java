package com.example.appdeviceconnect.network;

import com.example.appdeviceconnect.models.Product;
import com.example.appdeviceconnect.models.CustomerRegisterDto;
import com.example.appdeviceconnect.models.Customer;
import com.example.appdeviceconnect.models.CustomerLoginDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // Get the list of products
    @GET("/api/products")
    Call<List<Product>> getProducts();

    // Register a new customer
    @POST("/api/Customer/register")
    Call<Void> registerCustomer(@Body CustomerRegisterDto customerDto);

    // Customer login
    @POST("/api/Customer/login")
    Call<Customer> loginCustomer(@Body CustomerLoginDto customerLoginDto);
}
