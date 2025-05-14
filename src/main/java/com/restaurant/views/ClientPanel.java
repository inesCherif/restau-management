package com.restaurant.views;

import com.restaurant.models.Client;
import com.restaurant.services.ClientService;
import com.restaurant.services.AuthenticationService;
import com.restaurant.services.OrderService;
import com.restaurant.services.DataConsistencyService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.ListSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class ClientPanel extends JPanel {
    private ClientService clientService;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton;

    public ClientPanel() {
        clientService = new ClientService();
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
        JLabel title = new JLabel("Clients");
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
        searchField.setToolTipText("Search by name or username...");

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
        String[] columns = {"Username", "Name", "DOB", "Address", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return comp;
            }
        };
        clientTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clientTable.setRowHeight(40);
        clientTable.setShowGrid(true);
        clientTable.setGridColor(new Color(180, 180, 180));
        clientTable.setIntercellSpacing(new Dimension(1, 1));
        clientTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20));
        clientTable.getTableHeader().setBackground(new Color(41, 128, 185));
        clientTable.getTableHeader().setForeground(Color.WHITE);
        clientTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        clientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientTable.setSelectionBackground(new Color(52, 152, 219, 50));
        clientTable.setSelectionForeground(new Color(41, 128, 185));

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
        for (int i = 0; i < clientTable.getColumnCount(); i++) {
            clientTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(clientTable);
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
        addButton.addActionListener(e -> onAddClient());
        editButton.addActionListener(e -> onEditClient());
        deleteButton.addActionListener(e -> onDeleteClient());
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
        List<Client> clients = clientService.getAllClients();
        if (!filter.isEmpty()) {
            String lower = filter.toLowerCase();
            clients = clients.stream().filter(c ->
                c.getUsername().toLowerCase().contains(lower) ||
                c.getName().toLowerCase().contains(lower)
            ).collect(Collectors.toList());
        }
        tableModel.setRowCount(0);
        for (Client c : clients) {
            tableModel.addRow(new Object[] {
                c.getUsername(),
                c.getName(),
                c.getDateOfBirth(),
                c.getAddress(),
                c.getPhone()
            });
        }
    }

    private void onAddClient() {
        ClientFormDialog dialog = new ClientFormDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        Client newClient = dialog.getResult();
        if (newClient != null) {
            // Check for unique username
            boolean exists = clientService.getAllClients().stream()
                .anyMatch(c -> c.getUsername().equalsIgnoreCase(newClient.getUsername()));
            if (exists) {
                showError("Username already exists. Please choose another.");
                return;
            }
            clientService.addClient(newClient);
            // Register client in authentication system
            AuthenticationService authService = new AuthenticationService();
            authService.registerUser(newClient);
            refreshTable(searchField.getText().trim());
        }
    }

    private void onEditClient() {
        int row = clientTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a client to edit.");
            return;
        }
        String username = (String) tableModel.getValueAt(row, 0);
        Client existing = clientService.getAllClients().stream()
            .filter(c -> c.getUsername().equals(username)).findFirst().orElse(null);
        if (existing == null) {
            showError("Selected client not found.");
            return;
        }
        ClientFormDialog dialog = new ClientFormDialog((Frame) SwingUtilities.getWindowAncestor(this), existing);
        dialog.setVisible(true);
        Client updated = dialog.getResult();
        if (updated != null) {
            // Find index in service
            List<Client> all = clientService.getAllClients();
            int idx = -1;
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getUsername().equals(username)) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                clientService.updateClient(idx, updated);
                refreshTable(searchField.getText().trim());
            }
        }
    }

    private void onDeleteClient() {
        int row = clientTable.getSelectedRow();
        if (row == -1) {
            showError("Please select a client to delete.");
            return;
        }
        String username = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete client '" + username + "'? This will also remove their account and orders.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Use DataConsistencyService to delete client, user, and orders
            AuthenticationService authService = new AuthenticationService();
            OrderService orderService = new OrderService();
            DataConsistencyService dataConsistencyService = new DataConsistencyService(clientService, authService, orderService);
            boolean success = dataConsistencyService.deleteClientAndRelatedData(username);
            if (!success) {
                showError("Failed to delete client. Client not found.");
            }
            refreshTable(searchField.getText().trim());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 