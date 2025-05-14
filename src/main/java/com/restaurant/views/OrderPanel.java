package com.restaurant.views;

import com.restaurant.models.Order;
import com.restaurant.models.MenuItem;
import com.restaurant.services.OrderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderPanel extends JPanel {
    private OrderService orderService;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton updateStatusButton, deleteButton;

    public OrderPanel() {
        orderService = new OrderService();
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
        JLabel title = new JLabel("Orders");
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
        searchField.setToolTipText("Search by order ID or client name...");

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
        String[] columns = {"Order ID", "Client", "Items", "Total", "Status", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return comp;
            }
        };
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderTable.setRowHeight(40);
        orderTable.setShowGrid(true);
        orderTable.setGridColor(new Color(180, 180, 180));
        orderTable.setIntercellSpacing(new Dimension(1, 1));
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20));
        orderTable.getTableHeader().setBackground(new Color(41, 128, 185));
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setSelectionBackground(new Color(52, 152, 219, 50));
        orderTable.setSelectionForeground(new Color(41, 128, 185));

        // Custom cell renderer for better text alignment and padding
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return comp;
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < orderTable.getColumnCount(); i++) {
            orderTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);
        updateStatusButton = createStyledButton("Update Status");
        deleteButton = createStyledButton("Delete");
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners for update status, delete
        updateStatusButton.addActionListener(e -> onUpdateStatus());
        deleteButton.addActionListener(e -> onDeleteOrder());
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
        List<Order> orders = orderService.getAllOrders();
        if (!filter.isEmpty()) {
            String lower = filter.toLowerCase();
            orders = orders.stream().filter(o ->
                o.getClientUsername().toLowerCase().contains(lower) ||
                o.getStatus().name().toLowerCase().contains(lower)
            ).collect(Collectors.toList());
        }
        tableModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Order o : orders) {
            String itemsStr = o.getItems().stream().map(MenuItem::getName).collect(Collectors.joining(", "));
            tableModel.addRow(new Object[] {
                o.getId(),
                o.getClientUsername(),
                itemsStr,
                o.getTotalPrice(),
                o.getStatus().name().replace('_', ' '),
                o.getTimestamp().format(dtf)
            });
        }
    }

    private void onUpdateStatus() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        Order existing = orderService.getAllOrders().stream()
            .filter(o -> o.getId().equals(id)).findFirst().orElse(null);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Selected order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Show status options as readable strings
        Order.Status[] statuses = Order.Status.values();
        String[] statusOptions = {"NOT PROCESSED", "IN PREPARATION", "READY", "OUT FOR DELIVERY"};
        int currentStatusIdx = existing.getStatus().ordinal();
        int selected = JOptionPane.showOptionDialog(
            this,
            "Select new status:",
            "Update Order Status",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            statusOptions[currentStatusIdx]
        );
        if (selected < 0) return; // Cancelled
        Order.Status newStatus = statuses[selected];
        if (newStatus != existing.getStatus()) {
            // Find index in service
            List<Order> all = orderService.getAllOrders();
            int idx = -1;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equals(id)) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                existing.setStatus(newStatus);
                orderService.updateOrder(idx, existing);
                refreshTable(searchField.getText().trim());
                JOptionPane.showMessageDialog(this, "Order status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void onDeleteOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this order?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Find index in service
            List<Order> all = orderService.getAllOrders();
            int idx = -1;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equals(id)) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                orderService.deleteOrder(idx);
                refreshTable(searchField.getText().trim());
            }
        }
    }
} 