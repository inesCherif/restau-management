package com.restaurant.models;

import java.time.LocalDate;

public class Manager extends User {
    public Manager(String username, String password, String name, LocalDate dateOfBirth, String address, String phone) {
        super(username, password, name, dateOfBirth, address, phone);
    }

    @Override
    public String getRole() {
        return "MANAGER";
    }
} 