package com.restaurant.models;

import java.io.Serializable;

public class MenuItem implements Serializable {
    public enum Type { MEAL, DESSERT, DRINK }
    private String name;
    private String description;
    private double price;
    private Type type;
    private String imagePath; // Optional: path to image/icon

    public MenuItem(String name, String description, double price, Type type, String imagePath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
} 