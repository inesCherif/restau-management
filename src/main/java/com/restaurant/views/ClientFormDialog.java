package com.restaurant.views;

import com.restaurant.models.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ClientFormDialog extends JDialog {
    private JTextField usernameField, nameField, dobField, addressField, phoneField;
    private JPasswordField passwordField;
    private JButton okButton, cancelButton;
    private Client result;
    private boolean isEditMode;

    public ClientFormDialog(Frame parent, Client existingClient) {
        super(parent, true);
        this.isEditMode = (existingClient != null);
        setTitle(isEditMode ? "Edit Client" : "Add Client");
        setSize(440, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        setupUI(existingClient);
    }

    private void setupUI(Client client) {
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(236, 240, 241), 0, getHeight(), new Color(189, 195, 199));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        bgPanel.setLayout(new BorderLayout());
        bgPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel(isEditMode ? "Edit Client" : "Add Client");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);
        gbc.gridy++;
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(189, 195, 199));
        sep.setPreferredSize(new Dimension(340, 2));
        formPanel.add(sep, gbc);
        gbc.gridwidth = 1;

        usernameField = createStyledField("Username");
        passwordField = new JPasswordField();
        styleField(passwordField);
        nameField = createStyledField("Name");
        dobField = createStyledField("Date of Birth (yyyy-mm-dd)");
        addressField = createStyledField("Address");
        phoneField = createStyledField("Phone");

        addLabeledField(formPanel, gbc, "Username", usernameField, 2);
        addLabeledField(formPanel, gbc, "Password", passwordField, 3);
        addLabeledField(formPanel, gbc, "Name", nameField, 4);
        addLabeledField(formPanel, gbc, "Date of Birth (yyyy-mm-dd)", dobField, 5);
        addLabeledField(formPanel, gbc, "Address", addressField, 6);
        addLabeledField(formPanel, gbc, "Phone", phoneField, 7);

        if (isEditMode && client != null) {
            usernameField.setText(client.getUsername());
            usernameField.setEditable(false);
            passwordField.setText(client.getPassword());
            nameField.setText(client.getName());
            dobField.setText(client.getDateOfBirth().toString());
            addressField.setText(client.getAddress());
            phoneField.setText(client.getPhone());
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        okButton = createStyledButton(isEditMode ? "Save" : "Add");
        cancelButton = createStyledButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        bgPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(bgPanel);

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> {
            result = null;
            dispose();
        });
    }

    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField();
        styleField(field);
        field.setToolTipText(placeholder);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(300, 36));
        field.setMaximumSize(new Dimension(300, 36));
        field.setBackground(new Color(255, 255, 255, 230));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(41, 128, 185), 2, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
        });
    }

    private void addLabeledField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l.setForeground(new Color(44, 62, 80));
        l.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(l, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 1, true),
            BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(52, 152, 219), 2, true),
                    BorderFactory.createEmptyBorder(8, 24, 8, 24)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(41, 128, 185), 1, true),
                    BorderFactory.createEmptyBorder(8, 24, 8, 24)
                ));
            }
        });
        return button;
    }

    private void onOK() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String name = nameField.getText().trim();
        String dobStr = dobField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || dobStr.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            showError("All fields are required.");
            return;
        }
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            showError("Date of Birth must be in yyyy-mm-dd format.");
            return;
        }
        result = new Client(username, password, name, dob, address, phone);
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public Client getResult() {
        return result;
    }
} 