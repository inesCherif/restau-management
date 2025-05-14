package com.restaurant.services;

import com.restaurant.models.Manager;
import com.restaurant.models.User;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthenticationService {
    private static final String USERS_FILE = "resources/data/users.dat";
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    private Map<String, User> users;
    private User currentUser;
    
    public AuthenticationService() {
        users = new HashMap<>();
        loadUsers();
    }
    
    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        System.out.println("[DEBUG] Authenticating user: " + username + " class: " + (user != null ? user.getClass().getName() : "null"));
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    public boolean registerUser(User user) {
        System.out.println("[DEBUG] Registering user: " + user.getUsername() + " class: " + user.getClass().getName());
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return saveUsers();
    }
    
    public boolean removeUser(String username) {
        if (users.remove(username) != null) {
            return saveUsers();
        }
        return false;
    }
    
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            // Create default manager account
            Manager defaultManager = new Manager(
                "admin",
                "admin123",
                "System Administrator",
                java.time.LocalDate.now(),
                "Restaurant Address",
                "123-456-7890"
            );
            users.put(defaultManager.getUsername(), defaultManager);
            if (!saveUsers()) {
                LOGGER.severe("Failed to save default manager account");
            }
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (Map<String, User>) ois.readObject();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading users file: " + e.getMessage());
            users = new HashMap<>();
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error deserializing users data: " + e.getMessage());
            users = new HashMap<>();
        }
    }
    
    private boolean saveUsers() {
        File directory = new File("resources/data");
        if (!directory.exists() && !directory.mkdirs()) {
            LOGGER.severe("Failed to create data directory");
            return false;
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving users to file: " + e.getMessage());
            return false;
        }
    }
} 