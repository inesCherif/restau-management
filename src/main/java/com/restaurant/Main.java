package com.restaurant;

import com.restaurant.models.User;
import com.restaurant.services.AuthenticationService;
import com.restaurant.views.LoginWindow;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static AuthenticationService authService;
    private static LoginWindow loginWindow;

    public static void main(String[] args) {
        // Initialize services
        try {
            authService = new AuthenticationService();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize authentication service: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "Failed to start the application. Please check the system logs.",
                "Startup Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        // Create and show login window
        SwingUtilities.invokeLater(() -> {
            try {
                loginWindow = new LoginWindow();
                
                // Add login listener
                loginWindow.addLoginListener(e -> handleLogin());
                
                loginWindow.setVisible(true);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to create login window: " + e.getMessage());
                JOptionPane.showMessageDialog(null,
                    "Failed to create login window. Please check the system logs.",
                    "UI Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    private static void handleLogin() {
        String username = loginWindow.getUsername();
        String password = loginWindow.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            loginWindow.showError("Please enter both username and password");
            return;
        }
        
        try {
            if (authService.authenticate(username, password)) {
                User user = authService.getCurrentUser();
                loginWindow.setVisible(false);
                loginWindow.dispose();
                
                if (user.getRole().equals("MANAGER")) {
                    new com.restaurant.views.ManagerDashboard();
                } else if (user instanceof com.restaurant.models.Client) {
                    new com.restaurant.views.ClientDashboard((com.restaurant.models.Client) user).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "User is not a valid client!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                // Clear fields for security
                loginWindow.clearFields();
            } else {
                loginWindow.showError("Invalid username or password");
                loginWindow.clearFields();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Authentication error", e);
            loginWindow.showError("An error occurred during login. Please try again.");
            loginWindow.clearFields();
        }
    }
} 