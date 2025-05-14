package com.restaurant.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private LoadingButton loginButton;
    private JPanel contentPanel;
    private float alpha = 0f;
    private Timer fadeTimer;
    private BufferedImage restaurantImage;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    // Custom button class with loading animation
    private class LoadingButton extends JButton {
        private boolean isLoading = false;
        private float angle = 0f;
        private Timer loadingTimer;
        
        public LoadingButton(String text) {
            super(text);
            loadingTimer = new Timer(50, e -> {
                angle += 10f;
                if (angle >= 360f) angle = 0f;
                repaint();
            });
            
            setPreferredSize(new Dimension(200, 45));
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isLoading) {
                // Draw loading animation
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw spinning circle
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3));
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.min(getWidth(), getHeight()) / 4;
                
                g2d.rotate(Math.toRadians(angle), centerX, centerY);
                g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 0, 270);
            } else {
                if (getModel().isPressed()) {
                    g2d.setColor(SECONDARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(SECONDARY_COLOR);
                } else {
                    g2d.setColor(PRIMARY_COLOR);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(Color.WHITE);
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        }
        
        public void setLoading(boolean loading) {
            isLoading = loading;
            if (loading) {
                loadingTimer.start();
            } else {
                loadingTimer.stop();
            }
            repaint();
        }
    }
    
    public LoginWindow() {
        setupWindow();
        loadResources();
        setupComponents();
        setupAnimations();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void loadResources() {
        try {
            // Try multiple ways to load the image
            URL imageUrl = null;
            
            // Method 1: Using class loader
            imageUrl = LoginWindow.class.getClassLoader().getResource("images/logo.png");
            
            // Method 2: Using class
            if (imageUrl == null) {
                imageUrl = LoginWindow.class.getResource("/images/logo.png");
            }
            
            // Method 3: Using system class loader
            if (imageUrl == null) {
                imageUrl = ClassLoader.getSystemResource("images/logo.png");
            }
            
            if (imageUrl != null) {
                System.out.println("Found image at: " + imageUrl);
                restaurantImage = ImageIO.read(imageUrl);
                if (restaurantImage == null) {
                    System.err.println("Failed to read image data");
                } else {
                    System.out.println("Successfully loaded image. Size: " + 
                        restaurantImage.getWidth() + "x" + restaurantImage.getHeight());
                }
            } else {
                System.err.println("Could not find logo.png in resources");
                System.err.println("Current class path: " + System.getProperty("java.class.path"));
                System.err.println("Current working directory: " + System.getProperty("user.dir"));
                
                // List all resources in the classpath
                try {
                    Enumeration<URL> resources = LoginWindow.class.getClassLoader().getResources("");
                    while (resources.hasMoreElements()) {
                        System.err.println("Resource root: " + resources.nextElement());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load restaurant image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupWindow() {
        setTitle("Restaurant Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 600));
        setResizable(true);
        
        // Set custom font
        try {
            Font customFont = new Font("Segoe UI", Font.PLAIN, 14);
            UIManager.put("TextField.font", customFont);
            UIManager.put("PasswordField.font", customFont);
            UIManager.put("Button.font", customFont);
            UIManager.put("Label.font", customFont);
        } catch (Exception e) {
            // Fallback to default font if Segoe UI is not available
            Font customFont = new Font("Arial", Font.PLAIN, 14);
            UIManager.put("TextField.font", customFont);
            UIManager.put("PasswordField.font", customFont);
            UIManager.put("Button.font", customFont);
            UIManager.put("Label.font", customFont);
        }
    }
    
    private void setupAnimations() {
        // Fade-in animation
        fadeTimer = new Timer(20, e -> {
            alpha += 0.05f;
            if (alpha >= 1f) {
                alpha = 1f;
                fadeTimer.stop();
            }
            contentPanel.repaint();
        });
        fadeTimer.start();
    }
    
    private void setupComponents() {
        // Main panel with background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(236, 240, 241),
                    getWidth(), getHeight(), new Color(189, 195, 199)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw subtle pattern
                g2d.setColor(new Color(255, 255, 255, 30));
                for (int i = 0; i < getWidth(); i += 20) {
                    for (int j = 0; j < getHeight(); j += 20) {
                        g2d.fillOval(i, j, 2, 2);
                    }
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Left panel for image
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185, 200),
                    getWidth(), getHeight(), new Color(52, 152, 219, 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                if (restaurantImage != null) {
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    
                    // Fill the height, maintain aspect ratio, center horizontally
                    double ratio = (double) restaurantImage.getWidth() / restaurantImage.getHeight();
                    int imgHeight = getHeight();
                    int imgWidth = (int) (imgHeight * ratio);
                    int x = (getWidth() - imgWidth) / 2;
                    int y = 0;
                    
                    g2d.drawImage(restaurantImage, x, y, imgWidth, imgHeight, null);
                } else {
                    // Draw fallback design
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                    String text = "Restaurant Management";
                    FontMetrics metrics = g2d.getFontMetrics();
                    int x = (getWidth() - metrics.stringWidth(text)) / 2;
                    int y = getHeight() / 2;
                    g2d.drawString(text, x, y);
                    
                    // Draw decorative elements
                    g2d.setStroke(new BasicStroke(2));
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.drawRoundRect(20, 20, getWidth() - 40, getHeight() - 40, 20, 20);
                }
            }
        };
        leftPanel.setPreferredSize(new Dimension(400, 0));
        
        // Content panel (white panel with shadow effect)
        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Apply fade-in effect
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
                // Draw white background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 15, 15);
            }
        };
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        contentPanel.setOpaque(false);
        
        // Title panel with animation
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username field with hover effect
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameLabel.setForeground(TEXT_COLOR);
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        usernameField = createStyledTextField();
        addHoverEffect(usernameField);
        formPanel.add(usernameField, gbc);
        
        // Password field with hover effect
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setForeground(TEXT_COLOR);
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        passwordField = createStyledPasswordField();
        addHoverEffect(passwordField);
        formPanel.add(passwordField, gbc);
        
        // Login button with loading animation
        loginButton = createStyledButton("Login");
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        
        // Add all panels to content panel
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add panels to main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void addHoverEffect(JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private LoadingButton createStyledButton(String text) {
        return new LoadingButton(text);
    }
    
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(e -> {
            loginButton.setLoading(true);
            listener.actionPerformed(e);
        });
    }
    
    public String getUsername() {
        return usernameField.getText();
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    public void showError(String message) {
        loginButton.setLoading(false);

        // Create custom error dialog with window decoration (close button)
        JDialog errorDialog = new JDialog(this, "Error", true);
        errorDialog.setSize(400, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Main panel with shadow and rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                // Draw white background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 15, 15);
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Standard warning icon
        Icon warningIcon = UIManager.getIcon("OptionPane.warningIcon");
        JLabel iconLabel = new JLabel(warningIcon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.add(iconLabel, BorderLayout.CENTER);

        // Error message
        JLabel errorMessage = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        errorMessage.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        errorMessage.setForeground(TEXT_COLOR);
        errorMessage.setHorizontalAlignment(SwingConstants.CENTER);
        errorMessage.setVerticalAlignment(SwingConstants.CENTER);

        // OK button
        JButton okButton = new LoadingButton("OK") {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(SECONDARY_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(SECONDARY_COLOR);
                } else {
                    g2d.setColor(PRIMARY_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        okButton.setPreferredSize(new Dimension(100, 40));
        okButton.addActionListener(e -> errorDialog.dispose());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        // Add components to main panel
        mainPanel.add(iconPanel, BorderLayout.NORTH);
        mainPanel.add(errorMessage, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        errorDialog.setContentPane(mainPanel);
        errorDialog.setVisible(true);
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        loginButton.setLoading(false);
    }
} 