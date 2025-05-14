package com.restaurant.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Order implements Serializable {
    public enum Status { NOT_PROCESSED, IN_PREPARATION, READY, OUT_FOR_DELIVERY }
    public enum DeliveryMode { HOME_DELIVERY, ON_SITE_PICKUP, TAKEAWAY }

    private String id;
    private String clientUsername;
    private List<MenuItem> items;
    private double totalPrice;
    private Status status;
    private DeliveryMode deliveryMode;
    private LocalDateTime timestamp;

    public Order(String id, String clientUsername, List<MenuItem> items, double totalPrice, Status status, DeliveryMode deliveryMode, LocalDateTime timestamp) {
        this.id = id;
        this.clientUsername = clientUsername;
        this.items = items;
        this.totalPrice = totalPrice;
        this.status = status;
        this.deliveryMode = deliveryMode;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getClientUsername() { return clientUsername; }
    public List<MenuItem> getItems() { return items; }
    public double getTotalPrice() { return totalPrice; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public DeliveryMode getDeliveryMode() { return deliveryMode; }
    public LocalDateTime getTimestamp() { return timestamp; }
} 