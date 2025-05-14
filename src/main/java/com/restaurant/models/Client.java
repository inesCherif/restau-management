package com.restaurant.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    private List<String> orderHistory;

    public Client(String username, String password, String name, LocalDate dateOfBirth, String address, String phone) {
        super(username, password, name, dateOfBirth, address, phone);
        this.orderHistory = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "CLIENT";
    }

    public List<String> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }

    public void addOrder(String orderId) {
        orderHistory.add(orderId);
    }
} 