package com.example.appdeviceconnect.network;

import com.example.appdeviceconnect.models.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("products")
    Call<List<Product>> getProducts();
}