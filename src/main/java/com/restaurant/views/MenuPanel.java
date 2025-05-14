package com.restaurant.views;

import com.restaurant.models.MenuItem;
import com.restaurant.services.MenuService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class MenuPanel extends JPanel {
    private MenuService menuService;
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton;

    public MenuPanel() {
        menuService = new MenuService();
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setupTopBar();
        setupTable();
        setupButtons();
        refreshTable("");
    }

    private void setupTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setOpaque(false);
        JLabel title = new JLabel("Menu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(41, 128, 185));
        topBar.add(title, BorderLayout.WEST);

        // Create search field with login-style UI
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBackground(new Color(255, 255, 255, 230));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(6, 40, 6, 12)
        ));
        searchField.setToolTipText("Search by name or description...");

        // Add search icon
        JLabel searchIcon = new JLabel();
        java.net.URL searchIconUrl = getClass().getResource("/images/search.png");
        if (searchIconUrl != null) {
            searchIcon.setIcon(new ImageIcon(new ImageIcon(searchIconUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        }
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Create a panel to hold the search field and icon
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Add hover and focus effects
        searchField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2, true),
                    BorderFactory.createEmptyBorder(6, 40, 6, 12)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!searchField.hasFocus()) {
                    searchField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                        BorderFactory.createEmptyBorder(6, 40, 6, 12)
                    ));
                }
            }
        });

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(41, 128, 185), 2, true),
                    BorderFactory.createEmptyBorder(6, 40, 6, 12)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                    BorderFactory.createEmptyBorder(6, 40, 6, 12)
                ));
            }
        });

        // Add search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshTable(searchField.getText().trim());
            }
        });

        topBar.add(searchPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);
    }

    private void setupTable() {
        String[] columns = {"Image", "Name", "Description", "Price", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class;
                if (columnIndex == 3) return Double.class;
                return String.class;
            }
        };
        menuTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return comp;
            }
        };
        menuTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuTable.setRowHeight(48);
        menuTable.setShowGrid(true);
        menuTable.setGridColor(new Color(180, 180, 180));
        menuTable.setIntercellSpacing(new Dimension(1, 1));
        menuTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20));
        menuTable.getTableHeader().setBackground(new Color(41, 128, 185));
        menuTable.getTableHeader().setForeground(Color.WHITE);
        menuTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.setSelectionBackground(new Color(52, 152, 219, 50));
        menuTable.setSelectionForeground(new Color(41, 128, 185));

        // Custom cell renderer for better text alignment and padding (for non-image columns)
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return comp;
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        // Only set the renderer for non-image columns (start from 1)
        for (int i = 1; i < menuTable.getColumnCount(); i++) {
            menuTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Special renderer for image column (column 0)
        menuTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setHorizontalAlignment(CENTER);
                setIcon((value instanceof ImageIcon) ? (ImageIcon) value : null);
                setText("");
                setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return this;
            }
        });

        menuTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        menuTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        menuTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);
        addButton = createStyledButton("Add");
        editButton = createStyledButton("Edit");
        deleteButton = createStyledButton("Delete");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners for add, edit, delete
        addButton.addActionListener(e -> onAddMenuItem());
        editButton.addActionListener(e -> onEditMenuItem());
        deleteButton.addActionListener(e -> onDeleteMenuItem());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
        });
        return button;
    }

    private void refreshTable(String filter) {
        List<MenuItem> items = menuService.getAllMenuItems();
        if (!filter.isEmpty()) {
            String lower = filter.toLowerCase();
            items = items.stream().filter(m ->
                m.getName().toLowerCase().contains(lower) ||
                m.getType().name().toLowerCase().contains(lower)
            ).collect(Collectors.toList());
        }
        tableModel.setRowCount(0);
        for (MenuItem m : items) {
            ImageIcon icon = null;
            String imagePath = m.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    if (imagePath.startsWith("/") && getClass().getResource(imagePath) != null) {
                        // Load from resources
                        java.net.URL imgURL = getClass().getResource(imagePath);
                        icon = new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                    } else {
                        // Load from file system
                        java.io.File imgFile = new java.io.File(imagePath);
                        if (imgFile.exists()) {
                            icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
                        }
                    }
                } catch (Exception ignored) {}
            }
            tableModel.addRow(new Object[] {
                icon,
                m.getName(),
                m.getDescription(),
                m.getPrice(),
                m.getType().name()
            });
        }
    }

    private void onAddMenuItem() {
        MenuItemFormDialog dialog = new MenuItemFormDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        MenuItem newItem = dialog.getResult();
        if (newItem != null) {
            // Check for unique name
            boolean exists = menuService.getAllMenuItems().stream()
                .anyMatch(m -> m.getName().equalsIgnoreCase(newItem.getName()));
            if (exists) {
                showError("Menu item name already exists. Please choose another.");
                return;
            }
            menuService.addMenuItem(newItem);
            refreshTable(searchField.getText().trim());
        }
    }

    private void onEditMenuItem() {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a menu item to edit.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 1);
        MenuItem existing = menuService.getAllMenuItems().stream()
            .filter(m -> m.getName().equals(name)).findFirst().orElse(null);
        if (existing == null) {
            showError("Selected menu item not found.");
            return;
        }
        MenuItemFormDialog dialog = new MenuItemFormDialog((Frame) SwingUtilities.getWindowAncestor(this), existing);
        dialog.setVisible(true);
        MenuItem updated = dialog.getResult();
        if (updated != null) {
            // Find index in service
            List<MenuItem> all = menuService.getAllMenuItems();
            int idx = -1;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getName().equals(name)) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                menuService.updateMenuItem(idx, updated);
                refreshTable(searchField.getText().trim());
            }
        }
    }

    private void onDeleteMenuItem() {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a menu item to delete.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete menu item '" + name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Find index in service
            List<MenuItem> all = menuService.getAllMenuItems();
            int idx = -1;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getName().equals(name)) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                menuService.deleteMenuItem(idx);
                refreshTable(searchField.getText().trim());
            }
        }
    }

    private void showThemedError(String message) {
        JDialog errorDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Error", true);
        errorDialog.setSize(340, 170);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 14, 14);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 14, 14);
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 8));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Icon and message side by side
        JPanel iconMsgPanel = new JPanel();
        iconMsgPanel.setOpaque(false);
        iconMsgPanel.setLayout(new BoxLayout(iconMsgPanel, BoxLayout.X_AXIS));
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        iconLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        msgArea.setForeground(new Color(44, 62, 80));
        msgArea.setOpaque(false);
        msgArea.setEditable(false);
        msgArea.setFocusable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setBorder(null);
        msgArea.setAlignmentY(Component.CENTER_ALIGNMENT);
        msgArea.setMaximumSize(new Dimension(220, 60));
        iconMsgPanel.add(iconLabel);
        iconMsgPanel.add(msgArea);

        // OK button
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        okButton.setBackground(new Color(41, 128, 185));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.setContentAreaFilled(false);
        okButton.setOpaque(true);
        okButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 1, true),
            BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                okButton.setBackground(new Color(52, 152, 219));
                okButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2, true),
                    BorderFactory.createEmptyBorder(8, 24, 8, 24)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                okButton.setBackground(new Color(41, 128, 185));
                okButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(41, 128, 185), 1, true),
                    BorderFactory.createEmptyBorder(8, 24, 8, 24)
                ));
            }
        });
        okButton.addActionListener(e -> errorDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        mainPanel.add(iconMsgPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        errorDialog.setContentPane(mainPanel);
        errorDialog.setVisible(true);
    }

    private void showError(String msg) {
        showThemedError(msg);
    }
} 