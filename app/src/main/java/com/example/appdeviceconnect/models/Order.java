package com.example.appdeviceconnect.models;

import java.util.List;

public class Order {
    private String id;
    private String customerId;
    private List<ProductOrder> products;  // List of products including vendor info
    private String address;
    private double totalPrice;
    private String orderStatus;
    private String orderDate;

    // Constructor with all fields
    public Order(String id, String customerId, List<ProductOrder> products, String address, double totalPrice, String orderStatus, String orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.products = products;
        this.address = address;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus != null ? orderStatus : "Pending";  // Default order status
        this.orderDate = orderDate;  // Set the current date in yyyy-mm-dd format
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<ProductOrder> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrder> products) {
        this.products = products;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
