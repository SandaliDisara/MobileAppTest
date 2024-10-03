package com.example.appdeviceconnect.models;

public class CustomerRegisterDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;

    public CustomerRegisterDto(String firstName, String lastName, String email, String password, String address, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters can be added if needed
}
