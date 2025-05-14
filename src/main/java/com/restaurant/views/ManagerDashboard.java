package com.restaurant.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerDashboard extends JFrame {
    private JPanel sidebar;
    private JPanel mainPanel;
    private JButton clientsButton, menuButton, ordersButton, specialOffersButton, logoutButton;
    private CardLayout cardLayout;

    public ManagerDashboard() {
        setupWindow();
        setupSidebar();
        setupMainPanel();
        setVisible(true);
    }

    private void setupWindow() {
        setTitle("Manager Dashboard - Restaurant Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(236, 240, 241));
    }

    private void setupSidebar() {
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), 0, getHeight(), new Color(52, 152, 219));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 10, 30, 10));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        Icon logoIcon = loadIcon("/images/restaurant-logo.png", 80, 80);
        if (logoIcon != null) logoLabel.setIcon(logoIcon);
        logoLabel.setText("<html><div style='text-align:center; color:white; font-size:18px; font-family:Segoe UI;'>Manager</div></html>");
        logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        logoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        logoLabel.setBorder(new EmptyBorder(0,0,30,0));
        sidebar.add(logoLabel);

        clientsButton = createSidebarButton("Clients", loadIcon("/images/clients.png", 28, 28));
        menuButton = createSidebarButton("Menu", loadIcon("/images/menu.png", 28, 28));
        ordersButton = createSidebarButton("Orders", loadIcon("/images/orders.png", 28, 28));
        specialOffersButton = createSidebarButton("Special Offers", loadIcon("/images/offers.png", 28, 28));
        logoutButton = createSidebarButton("Logout", loadIcon("/images/logout.png", 28, 28));

        sidebar.add(clientsButton);
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

    private void setupMainPanel() {
        cardLayout = new CardLayout();
        
        class AnimatedPanel extends JPanel {
            private float alpha = 1.0f;
            private Timer fadeTimer;
            private String nextPanel;
            private CardLayout cardLayout;

            public AnimatedPanel(CardLayout layout) {
                super(layout);
                this.cardLayout = layout;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (alpha < 1.0f) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }

            public void fadeToPanel(String panelName) {
                nextPanel = panelName;
                if (fadeTimer != null && fadeTimer.isRunning()) {
                    fadeTimer.stop();
                }
                alpha = 1.0f;
                fadeTimer = new Timer(16, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        alpha -= 0.1f;
                        if (alpha <= 0) {
                            alpha = 0;
                            fadeTimer.stop();
                            cardLayout.show(AnimatedPanel.this, nextPanel);
                            fadeIn();
                        }
                        repaint();
                    }
                });
                fadeTimer.start();
            }

            private void fadeIn() {
                if (fadeTimer != null && fadeTimer.isRunning()) {
                    fadeTimer.stop();
                }
                alpha = 0.0f;
                fadeTimer = new Timer(16, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        alpha += 0.1f;
                        if (alpha >= 1) {
                            alpha = 1.0f;
                            fadeTimer.stop();
                        }
                        repaint();
                    }
                });
                fadeTimer.start();
            }
        }

        mainPanel = new AnimatedPanel(cardLayout);
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Use the real panels for each section
        JPanel clientsPanel = new ClientPanel();
        JPanel menuPanel = new MenuPanel();
        JPanel ordersPanel = new OrderPanel();
        JPanel specialOffersPanel = new SpecialOffersPanel(SpecialOffersPanel.Mode.MANAGER);

        mainPanel.add(clientsPanel, "clients");
        mainPanel.add(menuPanel, "menu");
        mainPanel.add(ordersPanel, "orders");
        mainPanel.add(specialOffersPanel, "special_offers");

        add(mainPanel, BorderLayout.CENTER);

        // Navigation logic with animations
        AnimatedPanel animatedPanel = (AnimatedPanel) mainPanel;
        clientsButton.addActionListener(e -> animatedPanel.fadeToPanel("clients"));
        menuButton.addActionListener(e -> animatedPanel.fadeToPanel("menu"));
        ordersButton.addActionListener(e -> animatedPanel.fadeToPanel("orders"));
        specialOffersButton.addActionListener(e -> animatedPanel.fadeToPanel("special_offers"));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });
    }

    private JPanel createPlaceholderPanel(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 247, 250));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(44, 62, 80));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JButton createSidebarButton(String text, Icon icon) {
        class AnimatedButton extends JButton {
            private float hoverAlpha = 0.0f;
            private Timer hoverTimer;

            public AnimatedButton(String text, Icon icon) {
                super(text, icon);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (hoverAlpha > 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, (int)(40 * hoverAlpha)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.dispose();
                }
            }

            public void animateHover(boolean enter) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                float targetAlpha = enter ? 1.0f : 0.0f;
                hoverTimer = new Timer(16, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (enter) {
                            hoverAlpha += 0.1f;
                            if (hoverAlpha >= targetAlpha) {
                                hoverAlpha = targetAlpha;
                                hoverTimer.stop();
            }
                        } else {
                            hoverAlpha -= 0.1f;
                            if (hoverAlpha <= targetAlpha) {
                                hoverAlpha = targetAlpha;
                                hoverTimer.stop();
                            }
                        }
                        repaint();
                    }
                });
                hoverTimer.start();
            }
        }

        AnimatedButton button = new AnimatedButton(text, icon);
        button.setMaximumSize(new Dimension(200, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0,0,0,0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(16);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover animation
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.animateHover(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.animateHover(false);
            }
        });

        return button;
    }

    private Icon loadIcon(String path, int w, int h) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                System.out.println("Loaded icon: " + path + " from " + imgURL);
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } else {
                System.err.println("Could not find icon: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path + " - " + e.getMessage());
        }
        return null;
    }
} 