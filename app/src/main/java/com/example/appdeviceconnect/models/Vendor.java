package com.example.appdeviceconnect.models;

import java.util.List;

public class Vendor {
    private String id;  // Vendor ID
    private String name;  // Vendor Name
    private String description;  // Vendor Description
    private double averageRanking;  // Calculated average ranking
    private List<Comment> comments;  // List of comments from customers

    // Default constructor
    public Vendor() {
    }

    // Parameterized constructor
    public Vendor(String id, String name, String description, double averageRanking, List<Comment> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.averageRanking = averageRanking;
        this.comments = comments;
    }

    // Getters and Setters for each field

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAverageRanking() {
        return averageRanking;
    }

    public void setAverageRanking(double averageRanking) {
        this.averageRanking = averageRanking;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // Optional: toString method for easier logging and debugging
    @Override
    public String toString() {
        return "Vendor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", averageRanking=" + averageRanking +
                ", comments=" + comments +
                '}';
    }
}
