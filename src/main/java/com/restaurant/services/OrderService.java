package com.restaurant.services;

import com.restaurant.models.Order;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private static final String ORDERS_FILE = "orders.dat";
    private List<Order> orders;

    public OrderService() {
        orders = loadOrders();
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public void addOrder(Order order) {
        orders.add(order);
        saveOrders();
    }

    public void updateOrder(int index, Order updated) {
        if (index >= 0 && index < orders.size()) {
            orders.set(index, updated);
            saveOrders();
        }
    }

    public void deleteOrder(int index) {
        if (index >= 0 && index < orders.size()) {
            orders.remove(index);
            saveOrders();
        }
    }

    private List<Order> loadOrders() {
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Order>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveOrders() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDERS_FILE))) {
            oos.writeObject(orders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 