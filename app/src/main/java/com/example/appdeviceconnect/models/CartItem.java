package com.example.appdeviceconnect.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String productId;  // ID of the product
    private String productName;  // Name of the product
    private String vendorId;  // ID of the vendor
    private String vendorName;  // Name of the vendor
    private int quantity;  // Quantity of the product added to the cart
    private double price;  // Price of the product


    // Constructor
    public CartItem(String productId, String productName, String vendorId, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.vendorId = vendorId;
        this.quantity = quantity;
        this.price = price;
    }

    // Parcelable constructor
    protected CartItem(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        vendorId = in.readString();
        vendorName = in.readString();
        quantity = in.readInt();
        price = in.readDouble();
    }

    // Parcelable Creator
    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // Getters and setters for each field

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

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Implement Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(vendorId);
        dest.writeString(vendorName);
        dest.writeInt(quantity);
        dest.writeDouble(price);
    }

    // Optional: toString method for easier logging and debugging
    @Override
    public String toString() {
        return "CartItem{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", vendorId='" + vendorId + '\'' +
                ", vendorName='" + vendorName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
