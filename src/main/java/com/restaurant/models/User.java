package com.restaurant.models;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class User implements Serializable {
    private String username;
    private String password;
    private String name;
    private LocalDate dateOfBirth;
    private String address;
    private String phone;
    private boolean isFirstLogin;

    public User(String username, String password, String name, LocalDate dateOfBirth, String address, String phone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.isFirstLogin = true;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public boolean isFirstLogin() { return isFirstLogin; }
    public void setFirstLogin(boolean isFirstLogin) { this.isFirstLogin = isFirstLogin; }

    // Abstract method to get user role
    public abstract String getRole();
} 