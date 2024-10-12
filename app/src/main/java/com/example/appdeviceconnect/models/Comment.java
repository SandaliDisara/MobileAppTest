package com.example.appdeviceconnect.models;

import java.io.Serializable;

public class Comment implements Serializable {
    private String customerId;  // Customer who made the comment
    private String customerName;  // Name of the customer
    private String text;  // Comment text
    private int ranking;  // Ranking score (1-5)

    // Default constructor (needed for deserialization)
    public Comment() {
    }

    // Constructor with fields
    public Comment(String customerId, String customerName, String text, int ranking) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.text = text;
        this.ranking = ranking;
    }

    // Getters and Setters for each field
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    // Optional: toString method for easier logging and debugging
    @Override
    public String toString() {
        return "Comment{" +
                "customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", text='" + text + '\'' +
                ", ranking=" + ranking +
                '}';
    }
}
