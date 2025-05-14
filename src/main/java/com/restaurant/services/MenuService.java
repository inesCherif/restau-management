package com.restaurant.services;

import com.restaurant.models.MenuItem;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MenuService {
    private static final String MENU_FILE = "menu.dat";
    private List<MenuItem> menuItems;

    public MenuService() {
        menuItems = loadMenuItems();
    }

    public List<MenuItem> getAllMenuItems() {
        return new ArrayList<>(menuItems);
    }

    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
        saveMenuItems();
    }

    public void updateMenuItem(int index, MenuItem updated) {
        if (index >= 0 && index < menuItems.size()) {
            menuItems.set(index, updated);
            saveMenuItems();
        }
    }

    public void deleteMenuItem(int index) {
        if (index >= 0 && index < menuItems.size()) {
            menuItems.remove(index);
            saveMenuItems();
        }
    }

    private List<MenuItem> loadMenuItems() {
        File file = new File(MENU_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<MenuItem>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveMenuItems() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MENU_FILE))) {
            oos.writeObject(menuItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 