package com.restaurant.views;

import com.restaurant.models.SpecialOffer;
import com.restaurant.services.SpecialOfferService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialOffersPanel extends JPanel {
    public enum Mode { MANAGER, CLIENT }
    private final Mode mode;
    private final SpecialOfferService offerService;
    private JTable offerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, deleteButton;

    public SpecialOffersPanel(Mode mode) {
        this.mode = mode;
        this.offerService = new SpecialOfferService();
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setupTopBar();
        setupTable();
        if (mode == Mode.MANAGER) setupButtons();
        refreshTable("");
    }

    private void setupTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setOpaque(false);
        JLabel title = new JLabel("Special Offers");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(41, 128, 185));
        topBar.add(title, BorderLayout.WEST);

        // Search field with icon
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBackground(new Color(255, 255, 255, 230));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(6, 40, 6, 12)
        ));
        searchField.setToolTipText("Search by title, description, or type...");
        JLabel searchIcon = new JLabel();
        java.net.URL searchIconUrl = getClass().getResource("/images/search.png");
        if (searchIconUrl != null) {
            searchIcon.setIcon(new ImageIcon(new ImageIcon(searchIconUrl).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        }
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
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
        String[] columns = {"Title", "Description", "Type", "Expiry"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        offerTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return comp;
            }
        };
        offerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        offerTable.setRowHeight(40);
        offerTable.setShowGrid(true);
        offerTable.setGridColor(new Color(180, 180, 180));
        offerTable.setIntercellSpacing(new Dimension(1, 1));
        offerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20));
        offerTable.getTableHeader().setBackground(new Color(41, 128, 185));
        offerTable.getTableHeader().setForeground(Color.WHITE);
        offerTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        offerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        offerTable.setSelectionBackground(new Color(52, 152, 219, 50));
        offerTable.setSelectionForeground(new Color(41, 128, 185));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return comp;
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int i = 0; i < offerTable.getColumnCount(); i++) {
            offerTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        JScrollPane scrollPane = new JScrollPane(offerTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);
        addButton = createStyledButton("Add Offer");
        deleteButton = createStyledButton("Delete Offer");
        addButton.addActionListener(e -> onAddOffer());
        deleteButton.addActionListener(e -> onDeleteOffer());
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        List<SpecialOffer> offers = (mode == Mode.MANAGER) ? offerService.getAllOffers() : offerService.getValidOffers();
        if (!filter.isEmpty()) {
            String f = filter.toLowerCase();
            offers = offers.stream().filter(o ->
                o.getTitle().toLowerCase().contains(f) ||
                o.getDescription().toLowerCase().contains(f) ||
                o.getOfferType().toLowerCase().contains(f)
            ).collect(Collectors.toList());
        }
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (SpecialOffer o : offers) {
            tableModel.addRow(new Object[] {
                o.getTitle(),
                o.getDescription(),
                o.getOfferType(),
                o.getValidUntil() != null ? o.getValidUntil().format(fmt) : "No expiry"
            });
        }
    }

    private void onAddOffer() {
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField expiryField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Title:")); panel.add(titleField);
        panel.add(new JLabel("Description:")); panel.add(descField);
        panel.add(new JLabel("Type:")); panel.add(typeField);
        panel.add(new JLabel("Expiry (yyyy-MM-dd, optional):")); panel.add(expiryField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Special Offer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String desc = descField.getText().trim();
            String type = typeField.getText().trim();
            String expiry = expiryField.getText().trim();
            if (title.isEmpty() || desc.isEmpty() || type.isEmpty()) {
                showError("All fields except expiry are required.");
                return;
            }
            LocalDate expiryDate = null;
            if (!expiry.isEmpty()) {
                try { expiryDate = LocalDate.parse(expiry); }
                catch (Exception ex) { showError("Invalid expiry date format."); return; }
            }
            offerService.addOffer(new SpecialOffer(title, desc, type, expiryDate));
            refreshTable(searchField.getText().trim());
        }
    }

    private void onDeleteOffer() {
        int row = offerTable.getSelectedRow();
        if (row == -1) { showError("Select an offer to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected offer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            offerService.deleteOffer(row);
            refreshTable(searchField.getText().trim());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 