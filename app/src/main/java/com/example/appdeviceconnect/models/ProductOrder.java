package com.example.appdeviceconnect.models;

public class ProductOrder {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private String productStatus;
    private String vendorId; // Vendor ID for tracking the vendor

    // Constructor
    public ProductOrder(String productId, String productName, int quantity, double price, String productStatus, String vendorId) {
        this.productId = productId;
        this.productName = productName;
        setQuantity(quantity); // Use setter to ensure validation
        setPrice(price);       // Use setter to ensure validation
        this.productStatus = productStatus;
        this.vendorId = vendorId;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", productStatus='" + productStatus + '\'' +
                ", vendorId='" + vendorId + '\'' +
                '}';
    }
}
