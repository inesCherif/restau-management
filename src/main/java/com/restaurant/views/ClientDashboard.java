package com.restaurant.views;

import com.restaurant.models.Client;
import com.restaurant.models.MenuItem;
import com.restaurant.models.Order;
import com.restaurant.services.MenuService;
import com.restaurant.services.OrderService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ClientDashboard extends JFrame {
    private JPanel sidebar;
    private JPanel contentPanel;
    private JButton homeButton, menuButton, ordersButton, specialOffersButton, logoutButton;
    private Client currentClient;
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private MenuService menuService;
    private OrderService orderService;
    private List<MenuItem> cartItems;
    private double cartTotal;
    private Timer animationTimer;
    private float animationProgress = 0f;
    private JPanel ordersGridRef;
    private JTextField ordersSearchFieldRef;

    public ClientDashboard(Client client) {
        this.currentClient = client;
        this.menuService = new MenuService();
        this.orderService = new OrderService();
        this.cartItems = new ArrayList<>();
        this.cartTotal = 0.0;
        setupWindow();
        setupSidebar();
        setupContentPanel();
        setupAnimations();
        pack();
        setLocationRelativeTo(null);
    }

    private void setupWindow() {
        setTitle("Restaurant Management System - Client Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 700));
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        
        // Add window icon safely
        java.net.URL iconUrl = getClass().getResource("/images/restaurant-icon.png");
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            setIconImage(icon.getImage());
        }
    }

    private void setupAnimations() {
        animationTimer = new Timer(16, e -> {
            animationProgress += 0.1f;
            if (animationProgress >= 1f) {
                animationProgress = 1f;
                animationTimer.stop();
            }
            repaint();
        });
    }

    private void setupSidebar() {
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 5));
                for (int i = 0; i < getHeight(); i += 4) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 0, 30, 0));

        // Profile section (no duplicate welcome panel)
        JPanel profilePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        profilePanel.setOpaque(false);
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel profilePic = new JLabel();
        profilePic.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePic.setPreferredSize(new Dimension(100, 100));
        profilePic.setMaximumSize(new Dimension(100, 100));
        java.net.URL profileImgUrl = getClass().getResource("/images/profile-placeholder.png");
        if (profileImgUrl != null) {
            ImageIcon profileIcon = new ImageIcon(new ImageIcon(profileImgUrl).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            profilePic.setIcon(profileIcon);
        }
        profilePanel.add(profilePic);
        profilePanel.add(Box.createVerticalStrut(15));

        // Add Change Password button under profile picture with improved styling
        JButton changePasswordButton = new JButton("Change Password") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 255, 255, 40));
                } else {
                    g2d.setColor(new Color(255, 255, 255, 20));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        changePasswordButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setOpaque(false);
        changePasswordButton.setContentAreaFilled(false);
        changePasswordButton.setBorderPainted(false);
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changePasswordButton.setMaximumSize(new Dimension(220, 36));
        changePasswordButton.setMinimumSize(new Dimension(220, 36));
        changePasswordButton.setPreferredSize(new Dimension(220, 36));
        changePasswordButton.setMargin(new Insets(0, 0, 0, 0));
        changePasswordButton.setText("Change Password");
        changePasswordButton.setHorizontalAlignment(SwingConstants.CENTER);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        profilePanel.add(changePasswordButton);
        profilePanel.add(Box.createVerticalStrut(10));

        sidebar.add(profilePanel);
        sidebar.add(Box.createVerticalStrut(20));

        // Sidebar buttons with improved width and hover/cursor
        homeButton = createSidebarButton("Home", "/images/home.png");
        menuButton = createSidebarButton("Menu", "/images/menu.png");
        ordersButton = createSidebarButton("My Orders", "/images/orders.png");
        specialOffersButton = createSidebarButton("Special Offers", "/images/offers.png");
        logoutButton = createSidebarButton("Logout", "/images/logout.png");

        homeButton.addActionListener(e -> animatePanelTransition("HOME"));
        menuButton.addActionListener(e -> animatePanelTransition("MENU"));
        ordersButton.addActionListener(e -> animatePanelTransition("ORDERS"));
        specialOffersButton.addActionListener(e -> animatePanelTransition("OFFERS"));
        logoutButton.addActionListener(e -> handleLogout());

        sidebar.add(homeButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(menuButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(ordersButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(specialOffersButton);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutButton);
        add(sidebar, BorderLayout.WEST);
    }

    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 40)); // semi-transparent white
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        button.setPreferredSize(new Dimension(250, 48));
        button.setMaximumSize(new Dimension(250, 48));
        button.setMinimumSize(new Dimension(250, 48));
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBackground(new Color(0,0,0,0));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(4);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        java.net.URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(iconUrl).getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH));
            button.setIcon(icon);
        }
        button.setText(text);
        return button;
    }

    private void animatePanelTransition(String panelName) {
        animationProgress = 0f;
        animationTimer.start();
        showPanel(panelName);
    }

    private void setupContentPanel() {
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add placeholder panels for each section
        contentPanel.add(createHomePanel(), "HOME");
        contentPanel.add(createMenuPanel(), "MENU");
        contentPanel.add(createOrdersPanel(), "ORDERS");
        contentPanel.add(new SpecialOffersPanel(SpecialOffersPanel.Mode.CLIENT), "OFFERS");

        // Show home panel by default
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "HOME");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Welcome section with modern design and fixed height
        JPanel welcomePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), 0, SECONDARY_COLOR);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(255, 255, 255, 5));
                for (int i = 0; i < getHeight(); i += 4) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
            }
        };
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        welcomePanel.setPreferredSize(new Dimension(0, 120));

        // Use BoxLayout for vertical stacking of welcome text
        JPanel welcomeTextPanel = new JPanel();
        welcomeTextPanel.setOpaque(false);
        welcomeTextPanel.setLayout(new BoxLayout(welcomeTextPanel, BoxLayout.Y_AXIS));
        welcomeTextPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeTitle = new JLabel("Welcome back, " + currentClient.getName() + "!");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel welcomeSubtitle = new JLabel("What would you like to order today?");
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcomeSubtitle.setForeground(new Color(255, 255, 255, 200));
        welcomeSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        welcomeTextPanel.add(welcomeTitle);
        welcomeTextPanel.add(Box.createVerticalStrut(8));
        welcomeTextPanel.add(welcomeSubtitle);

        welcomePanel.add(welcomeTextPanel, BorderLayout.WEST);

        // Quick actions section with image icons
        JPanel quickActionsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.add(createQuickActionCard("Browse Menu", "/images/menu.png", "Explore our delicious dishes", () -> showPanel("MENU")));
        quickActionsPanel.add(createQuickActionCard("My Orders", "/images/orders.png", "Track your orders", () -> showPanel("ORDERS")));
        quickActionsPanel.add(createQuickActionCard("Special Offers", "/images/offers.png", "Check out our latest deals", () -> showPanel("OFFERS")));

        // Recent orders section, left-aligned
        JPanel recentOrdersPanel = new JPanel(new BorderLayout(10, 10));
        recentOrdersPanel.setOpaque(false);
        recentOrdersPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        JLabel recentOrdersTitle = new JLabel("Recent Orders");
        recentOrdersTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        recentOrdersTitle.setForeground(TEXT_COLOR);
        recentOrdersTitle.setHorizontalAlignment(SwingConstants.LEFT);
        recentOrdersPanel.add(recentOrdersTitle, BorderLayout.NORTH);

        // Get recent orders
        List<Order> recentOrders = orderService.getAllOrders().stream()
            .filter(o -> o.getClientUsername().equals(currentClient.getUsername()))
            .sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
            .limit(3)
            .collect(Collectors.toList());

        JPanel ordersList = new JPanel();
        ordersList.setLayout(new BoxLayout(ordersList, BoxLayout.Y_AXIS));
        ordersList.setOpaque(false);

        if (recentOrders.isEmpty()) {
            JLabel noOrdersLabel = new JLabel("No recent orders") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0, 0, 0, 100));
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(getText(), x, y);
                }
            };
            noOrdersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            ordersList.add(noOrdersLabel);
        } else {
            for (Order order : recentOrders) {
                JPanel orderPanel = createRecentOrderPanel(order);
                ordersList.add(orderPanel);
                ordersList.add(Box.createVerticalStrut(15));
            }
        }

        recentOrdersPanel.add(ordersList, BorderLayout.CENTER);

        panel.add(welcomePanel, BorderLayout.NORTH);
        panel.add(quickActionsPanel, BorderLayout.CENTER);
        panel.add(recentOrdersPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRecentOrderPanel(Order order) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Order ID and status
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        JLabel idLabel = new JLabel("Order #" + order.getId());
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        idLabel.setForeground(TEXT_COLOR);

        JLabel statusLabel = new JLabel(order.getStatus().name().replace('_', ' ')) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw status badge
                Color statusColor;
                switch (order.getStatus()) {
                    case NOT_PROCESSED:
                        statusColor = WARNING_COLOR;
                        break;
                    case IN_PREPARATION:
                        statusColor = PRIMARY_COLOR;
                        break;
                    case READY:
                        statusColor = SUCCESS_COLOR;
                        break;
                    case OUT_FOR_DELIVERY:
                        statusColor = SECONDARY_COLOR;
                        break;
                    default:
                        statusColor = TEXT_COLOR;
                }
                g2d.setColor(statusColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        statusLabel.setPreferredSize(new Dimension(120, 25));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(idLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Order details
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 0));
        detailsPanel.setOpaque(false);

        JLabel itemsLabel = new JLabel(order.getItems().size() + " items • " + 
            order.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
        itemsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemsLabel.setForeground(new Color(0, 0, 0, 150));

        JLabel totalLabel = new JLabel(String.format("$%.2f", order.getTotalPrice()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY_COLOR);

        detailsPanel.add(itemsLabel, BorderLayout.WEST);
        detailsPanel.add(totalLabel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);

        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel("ORDERS");
            }
        });

        return panel;
    }

    private JPanel createQuickActionCard(String title, String iconPath, String description, Runnable onClick) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        java.net.URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(iconUrl).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            iconLabel.setIcon(icon);
        }
        iconLabel.setPreferredSize(new Dimension(40, 40));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(44, 62, 80, 150));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        // Add hover and click effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                card.setBorder(new EmptyBorder(20, 20, 20, 20));
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) onClick.run();
            }
        });
        return card;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top bar with search and filter
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setOpaque(false);

        // Search field with image icon
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField() {
            private Color borderColor = new Color(180, 200, 230);
            private Color focusColor = PRIMARY_COLOR;
            private float anim = 0f;
            private boolean focused = false;
            private Timer animTimer = new Timer(15, e -> {
                if (focused && anim < 1f) { anim += 0.08f; if (anim > 1f) anim = 1f; repaint(); }
                else if (!focused && anim > 0f) { anim -= 0.08f; if (anim < 0f) anim = 0f; repaint(); }
            });
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2d.setColor(new Color(255,255,255, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                // Shadow/Glow on focus
                if (anim > 0f) {
                    g2d.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), (int)(40*anim)));
                    g2d.setStroke(new BasicStroke(3f));
                    g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 18, 18);
                }
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = focused ? focusColor : borderColor;
                g2d.setColor(c);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
            }
            @Override
            public void addNotify() {
                super.addNotify();
                addFocusListener(new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusGained(java.awt.event.FocusEvent e) { focused = true; animTimer.start(); }
                    @Override
                    public void focusLost(java.awt.event.FocusEvent e) { focused = false; animTimer.start(); }
                });
            }
        };
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setOpaque(false);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 10));
        JLabel searchIcon = new JLabel();
        java.net.URL searchIconUrl = getClass().getResource("/images/search.png");
        if (searchIconUrl != null) {
            searchIcon.setIcon(new ImageIcon(new ImageIcon(searchIconUrl).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        }
        searchIcon.setPreferredSize(new Dimension(32, 32));
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12)); // 12px space to the right
        iconPanel.add(searchIcon, BorderLayout.CENTER);
        searchPanel.add(iconPanel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Filter combo box with modern design
        JComboBox<MenuItem.Type> filterCombo = new JComboBox<MenuItem.Type>(MenuItem.Type.values());
        filterCombo.setPreferredSize(new Dimension(200, 40));
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        filterCombo.setOpaque(false);
        filterCombo.setBackground(Color.WHITE);
        filterCombo.setForeground(TEXT_COLOR);
        filterCombo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        javax.swing.plaf.basic.BasicComboBoxRenderer renderer = new javax.swing.plaf.basic.BasicComboBoxRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        filterCombo.setRenderer(renderer);
        // Custom ComboBox UI
        filterCombo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int w = getWidth(), h = getHeight();
                        g2.setColor(Color.WHITE);
                        g2.fillRect(0, 0, w, h);
                        int size = 12;
                        int x = (w - size) / 2;
                        int y = (h - size) / 2 + 2;
                        g2.setColor(PRIMARY_COLOR);
                        int[] xs = {x, x + size, x + size / 2};
                        int[] ys = {y, y, y + size / 2};
                        g2.fillPolygon(xs, ys, 3);
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                return button;
            }
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 14, 14);
                if (hasFocus) {
                    g2.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 40));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(bounds.x, bounds.y, bounds.width-1, bounds.height-1, 14, 14);
                }
            }
        });

        topBar.add(searchPanel, BorderLayout.WEST);
        topBar.add(filterCombo, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Menu grid with modern card design
        JPanel menuGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        menuGrid.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(menuGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        // Custom ScrollBar UI
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = PRIMARY_COLOR;
                this.thumbDarkShadowColor = PRIMARY_COLOR.darker();
                this.thumbHighlightColor = PRIMARY_COLOR.brighter();
                this.trackColor = new Color(0,0,0,0);
                this.trackHighlightColor = new Color(0,0,0,0);
            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setVisible(false);
                return button;
            }
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 12, 12);
                if (isThumbRollover()) {
                    g2.setColor(PRIMARY_COLOR.brighter());
                    g2.drawRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width-1, thumbBounds.height-1, 12, 12);
                }
                g2.dispose();
            }
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,0));
                g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 12, 12);
                g2.dispose();
            }
        });
        panel.add(scrollPane, BorderLayout.CENTER);

        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout(15, 15));
        cartPanel.setOpaque(false);
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Cart items panel
        JPanel cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setOpaque(false);
        JScrollPane cartScrollPane = new JScrollPane(cartItemsPanel);
        cartScrollPane.setPreferredSize(new Dimension(300, 300));
        cartScrollPane.setBorder(BorderFactory.createEmptyBorder());
        cartScrollPane.setOpaque(false);
        cartScrollPane.getViewport().setOpaque(false);

        // Total label with modern design
        JLabel totalLabel = new JLabel("Total: $0.00") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw border
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                // Draw text
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        totalLabel.setPreferredSize(new Dimension(300, 50));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Checkout button with modern design
        JButton checkoutButton = new JButton("Checkout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                    g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        checkoutButton.setPreferredSize(new Dimension(300, 50));
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setOpaque(false);
        checkoutButton.setContentAreaFilled(false);
        checkoutButton.setBorderPainted(false);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutButton.addActionListener(e -> handleCheckout());

        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        cartPanel.add(totalLabel, BorderLayout.SOUTH);
        cartPanel.add(checkoutButton, BorderLayout.SOUTH);

        // Add cart panel to the right
        panel.add(cartPanel, BorderLayout.EAST);

        // Add document listener for search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshMenuGrid(menuGrid, searchField, filterCombo, cartItemsPanel, totalLabel); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshMenuGrid(menuGrid, searchField, filterCombo, cartItemsPanel, totalLabel); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshMenuGrid(menuGrid, searchField, filterCombo, cartItemsPanel, totalLabel); }
        });

        // Add action listener for filter
        filterCombo.addActionListener(e -> refreshMenuGrid(menuGrid, searchField, filterCombo, cartItemsPanel, totalLabel));

        // Initial refresh
        refreshMenuGrid(menuGrid, searchField, filterCombo, cartItemsPanel, totalLabel);

        return panel;
    }

    private void refreshMenuGrid(JPanel menuGrid, JTextField searchField, JComboBox<MenuItem.Type> filterCombo, 
                               JPanel cartItemsPanel, JLabel totalLabel) {
        menuGrid.removeAll();
        String searchText = searchField.getText().toLowerCase();
        MenuItem.Type selectedType = (MenuItem.Type) filterCombo.getSelectedItem();

        List<MenuItem> items = menuService.getAllMenuItems().stream()
            .filter(item -> {
                boolean matchesSearch = item.getName().toLowerCase().contains(searchText) ||
                                      item.getDescription().toLowerCase().contains(searchText);
                boolean matchesType = selectedType == null || item.getType() == selectedType;
                return matchesSearch && matchesType;
            })
            .collect(Collectors.toList());

        for (MenuItem item : items) {
            menuGrid.add(createMenuItemCard(item, cartItemsPanel, totalLabel));
        }

        menuGrid.revalidate();
        menuGrid.repaint();
    }

    private JPanel createMenuItemCard(MenuItem item, JPanel cartItemsPanel, JLabel totalLabel) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Image at the top
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(160, 90));
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            java.io.File imgFile = new java.io.File(item.getImagePath());
            ImageIcon icon = null;
            if (imgFile.exists()) {
                icon = new ImageIcon(new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(160, 90, Image.SCALE_SMOOTH));
            } else {
                java.net.URL imgUrl = getClass().getResource(item.getImagePath());
                if (imgUrl != null) {
                    icon = new ImageIcon(new ImageIcon(imgUrl).getImage().getScaledInstance(160, 90, Image.SCALE_SMOOTH));
                }
            }
            if (icon != null) imageLabel.setIcon(icon);
        }
        card.add(imageLabel, BorderLayout.NORTH);

        // Type badge at top-left (absolute positioning)
        JPanel badgePanel = new JPanel(null);
        badgePanel.setOpaque(false);
        badgePanel.setPreferredSize(new Dimension(0, 0));
        JLabel typeBadge = new JLabel(item.getType().name());
        typeBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        typeBadge.setForeground(Color.WHITE);
        typeBadge.setBackground(PRIMARY_COLOR);
        typeBadge.setOpaque(true);
        typeBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        typeBadge.setBounds(8, 8, 60, 20);
        badgePanel.add(typeBadge);
        card.add(badgePanel, BorderLayout.WEST);

        // Name, description, price
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        JLabel descLabel = new JLabel("<html><body style='width: 150px'>" + item.getDescription() + "</body></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setForeground(new Color(0, 0, 0, 150));
        JLabel priceLabel = new JLabel(String.format("$%.2f", item.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(PRIMARY_COLOR);
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(descLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(priceLabel);
        card.add(textPanel, BorderLayout.CENTER);

        // Add to cart button (unchanged)
        JButton addButton = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        addButton.setPreferredSize(new Dimension(36, 36));
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(false);
        addButton.setContentAreaFilled(false);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> {
            cartItems.add(item);
            cartTotal += item.getPrice();
            refreshCart(cartItemsPanel, totalLabel);
        });
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        addPanel.setOpaque(false);
        addPanel.add(addButton);
        card.add(addPanel, BorderLayout.SOUTH);

        // Add hover effect (unchanged)
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(6, 6, 6, 6)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                card.setBorder(new EmptyBorder(8, 8, 8, 8));
            }
        });
        return card;
    }

    private void refreshCart(JPanel cartItemsPanel, JLabel totalLabel) {
        cartItemsPanel.removeAll();
        
        for (MenuItem item : cartItems) {
            JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
            itemPanel.setOpaque(false);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JLabel nameLabel = new JLabel(item.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameLabel.setForeground(TEXT_COLOR);

            JLabel priceLabel = new JLabel(String.format("$%.2f", item.getPrice()));
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            priceLabel.setForeground(PRIMARY_COLOR);

            JButton removeButton = new JButton("×") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isPressed()) {
                        g2d.setColor(new Color(231, 76, 60).darker());
                    } else if (getModel().isRollover()) {
                        g2d.setColor(new Color(231, 76, 60));
                    } else {
                        g2d.setColor(new Color(231, 76, 60, 150));
                    }
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.setColor(Color.WHITE);
                    FontMetrics metrics = g2d.getFontMetrics();
                    int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                    int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                    g2d.drawString(getText(), x, y);
                }
            };
            removeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            removeButton.setForeground(Color.WHITE);
            removeButton.setFocusPainted(false);
            removeButton.setBorderPainted(false);
            removeButton.setContentAreaFilled(false);
            removeButton.setPreferredSize(new Dimension(24, 24));
            removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            removeButton.addActionListener(e -> {
                cartItems.remove(item);
                cartTotal -= item.getPrice();
                refreshCart(cartItemsPanel, totalLabel);
            });

            itemPanel.add(nameLabel, BorderLayout.WEST);
            itemPanel.add(priceLabel, BorderLayout.CENTER);
            itemPanel.add(removeButton, BorderLayout.EAST);

            cartItemsPanel.add(itemPanel);
            cartItemsPanel.add(Box.createVerticalStrut(5));
        }

        totalLabel.setText(String.format("Total: $%.2f", cartTotal));
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty. Add some items first!",
                "Empty Cart",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Delivery mode selection dialog
        String[] modes = {"Home Delivery", "On-site Pickup", "Takeaway"};
        int selected = JOptionPane.showOptionDialog(
            this,
            "Choose your delivery mode:",
            "Delivery Mode",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            modes,
            modes[0]
        );
        if (selected < 0) return; // Cancelled

        com.restaurant.models.Order.DeliveryMode deliveryMode;
        switch (selected) {
            case 0: deliveryMode = com.restaurant.models.Order.DeliveryMode.HOME_DELIVERY; break;
            case 1: deliveryMode = com.restaurant.models.Order.DeliveryMode.ON_SITE_PICKUP; break;
            case 2: deliveryMode = com.restaurant.models.Order.DeliveryMode.TAKEAWAY; break;
            default: deliveryMode = com.restaurant.models.Order.DeliveryMode.HOME_DELIVERY;
        }

        // Generate unique order ID (timestamp + random)
        String orderId = "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random()*1000);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        com.restaurant.models.Order order = new com.restaurant.models.Order(
            orderId,
            currentClient.getUsername(),
            new ArrayList<>(cartItems),
            cartTotal,
            com.restaurant.models.Order.Status.NOT_PROCESSED,
            deliveryMode,
            now
        );
        orderService.addOrder(order);
        // Optionally update client order history
        currentClient.addOrder(orderId);
        // Clear cart
        cartItems.clear();
        cartTotal = 0.0;
        // Refresh UI
        refreshCart(null, null);
        JOptionPane.showMessageDialog(this, "Order placed successfully! You can track it in 'My Orders'.", "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
        showPanel("ORDERS");
        // Refresh orders grid immediately
        if (ordersGridRef != null && ordersSearchFieldRef != null) {
            refreshOrdersGrid(ordersGridRef, ordersSearchFieldRef);
        }
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
        
        // Update button styles
        homeButton.setSelected(panelName.equals("HOME"));
        menuButton.setSelected(panelName.equals("MENU"));
        ordersButton.setSelected(panelName.equals("ORDERS"));
        specialOffersButton.setSelected(panelName.equals("OFFERS"));
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginWindow().setVisible(true);
        }
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top bar with search
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setOpaque(false);

        JLabel title = new JLabel("My Orders") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 28));
                FontMetrics fm = g2d.getFontMetrics();
                int x = 0;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        title.setPreferredSize(new Dimension(200, 40));

        // Search field with modern design
        JTextField searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2d.drawString("🔍", 10, getHeight() / 2 + 5);
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                super.paintComponent(g);
            }
        };
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 35, 0, 10));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setOpaque(false);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Orders grid with modern card design
        JPanel ordersGrid = new JPanel();
        ordersGrid.setLayout(new BoxLayout(ordersGrid, BoxLayout.Y_AXIS));
        ordersGrid.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(ordersGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Store references for later refresh
        ordersGridRef = ordersGrid;
        ordersSearchFieldRef = searchField;

        // Add document listener for search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshOrdersGrid(ordersGrid, searchField); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshOrdersGrid(ordersGrid, searchField); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshOrdersGrid(ordersGrid, searchField); }
        });

        // Initial refresh
        refreshOrdersGrid(ordersGrid, searchField);

        return panel;
    }

    private void refreshOrdersGrid(JPanel ordersGrid, JTextField searchField) {
        ordersGrid.removeAll();
        String filter = searchField.getText().trim().toLowerCase();
        
        // Get orders for current client
        List<Order> orders = orderService.getAllOrders().stream()
            .filter(o -> o.getClientUsername().equals(currentClient.getUsername()))
            .filter(o -> filter.isEmpty() || 
                o.getId().toLowerCase().contains(filter) ||
                o.getStatus().name().toLowerCase().contains(filter))
            .collect(Collectors.toList());

        if (orders.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout(20, 20));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(50, 0, 0, 0));

            JLabel emptyLabel = new JLabel("No orders found") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(0, 0, 0, 100));
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 24));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(getText(), x, y);
                }
            };
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            ordersGrid.add(emptyPanel);
        } else {
            for (Order order : orders) {
                ordersGrid.add(createOrderCard(order));
                ordersGrid.add(Box.createVerticalStrut(20));
            }
        }

        ordersGrid.revalidate();
        ordersGrid.repaint();
    }

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                
                // Draw status badge
                Color statusColor;
                switch (order.getStatus()) {
                    case NOT_PROCESSED:
                        statusColor = WARNING_COLOR;
                        break;
                    case IN_PREPARATION:
                        statusColor = PRIMARY_COLOR;
                        break;
                    case READY:
                        statusColor = SUCCESS_COLOR;
                        break;
                    case OUT_FOR_DELIVERY:
                        statusColor = SECONDARY_COLOR;
                        break;
                    default:
                        statusColor = TEXT_COLOR;
                }
                g2d.setColor(statusColor);
                g2d.fillRoundRect(10, 10, 120, 25, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String status = order.getStatus().name().replace('_', ' ');
                int x = 10 + (120 - fm.stringWidth(status)) / 2;
                int y = 10 + (25 + fm.getAscent()) / 2;
                g2d.drawString(status, x, y);
            }
        };
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Order details
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setOpaque(false);

        // Order ID and timestamp
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);

        JLabel idLabel = new JLabel("Order #" + order.getId());
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        idLabel.setForeground(TEXT_COLOR);

        JLabel timeLabel = new JLabel(order.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(0, 0, 0, 150));

        headerPanel.add(idLabel, BorderLayout.WEST);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        // Items list
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        for (MenuItem item : order.getItems()) {
            JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
            itemPanel.setOpaque(false);

            JLabel itemName = new JLabel(item.getName());
            itemName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            itemName.setForeground(TEXT_COLOR);

            JLabel itemPrice = new JLabel(String.format("$%.2f", item.getPrice()));
            itemPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
            itemPrice.setForeground(PRIMARY_COLOR);

            itemPanel.add(itemName, BorderLayout.WEST);
            itemPanel.add(itemPrice, BorderLayout.EAST);
            itemsPanel.add(itemPanel);
            itemsPanel.add(Box.createVerticalStrut(5));
        }

        // Total and delivery mode
        JPanel footerPanel = new JPanel(new BorderLayout(10, 0));
        footerPanel.setOpaque(false);

        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", order.getTotalPrice()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY_COLOR);

        JLabel deliveryLabel = new JLabel(order.getDeliveryMode().name().replace('_', ' '));
        deliveryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deliveryLabel.setForeground(new Color(0, 0, 0, 150));

        footerPanel.add(totalLabel, BorderLayout.WEST);
        footerPanel.add(deliveryLabel, BorderLayout.EAST);

        detailsPanel.add(headerPanel, BorderLayout.NORTH);
        detailsPanel.add(itemsPanel, BorderLayout.CENTER);
        detailsPanel.add(footerPanel, BorderLayout.SOUTH);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        if (order.getStatus() == Order.Status.NOT_PROCESSED) {
            JButton cancelButton = createActionButton("Cancel", ERROR_COLOR);
            cancelButton.addActionListener(e -> handleCancelOrder(order));
            buttonPanel.add(cancelButton);
        }

        card.add(detailsPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(13, 13, 13, 13)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                card.setBorder(new EmptyBorder(15, 15, 15, 15));
            }
        });

        return card;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        button.setPreferredSize(new Dimension(100, 32));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleCancelOrder(Order order) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this order?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Find index in service
            List<Order> all = orderService.getAllOrders();
            int idx = -1;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equals(order.getId())) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                orderService.deleteOrder(idx);
                refreshOrdersGrid((JPanel) ((JScrollPane) ((JPanel) getContentPane().getComponent(1)).getComponent(1)).getViewport().getView(), null);
            }
        }
    }

    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();
        panel.add(new JLabel("Old Password:"));
        panel.add(oldPass);
        panel.add(new JLabel("New Password:"));
        panel.add(newPass);
        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmPass);
        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String oldP = new String(oldPass.getPassword());
            String newP = new String(newPass.getPassword());
            String confirmP = new String(confirmPass.getPassword());
            // Validate old password
            if (!oldP.equals(currentClient.getPassword())) {
                JOptionPane.showMessageDialog(this, "Old password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Validate new password requirements
            if (newP.length() < 8 || !newP.matches(".*\\d.*")) {
                JOptionPane.showMessageDialog(this, "New password must be at least 8 characters and contain at least one number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newP.equals(confirmP)) {
                JOptionPane.showMessageDialog(this, "New password and confirmation do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Update password in currentClient and AuthenticationService
            currentClient.setPassword(newP);
            com.restaurant.services.AuthenticationService authService = new com.restaurant.services.AuthenticationService();
            authService.registerUser(currentClient); // Overwrites user with new password
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 