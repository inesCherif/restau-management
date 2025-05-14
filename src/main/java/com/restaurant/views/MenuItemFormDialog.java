package com.restaurant.views;

import com.restaurant.models.MenuItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MenuItemFormDialog extends JDialog {
    private JTextField nameField, priceField, imagePathField;
    private JTextArea descriptionArea;
    private JComboBox<MenuItem.Type> typeCombo;
    private JButton okButton, cancelButton, browseButton;
    private MenuItem result;
    private boolean isEditMode;

    public MenuItemFormDialog(Frame parent, MenuItem existingItem) {
        super(parent, true);
        this.isEditMode = (existingItem != null);
        setTitle(isEditMode ? "Edit Menu Item" : "Add Menu Item");
        setSize(480, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        setupUI(existingItem);
    }

    private void setupUI(MenuItem item) {
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

        JLabel titleLabel = new JLabel(isEditMode ? "Edit Menu Item" : "Add Menu Item");
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

        nameField = createStyledField();
        addLabeledField(formPanel, gbc, "Name", nameField, 2);

        descriptionArea = new JTextArea(3, 20);
        styleTextArea(descriptionArea);
        addLabeledField(formPanel, gbc, "Description", new JScrollPane(descriptionArea), 3);

        priceField = createStyledField();
        addLabeledField(formPanel, gbc, "Price", priceField, 4);

        typeCombo = new JComboBox<>(MenuItem.Type.values());
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        typeCombo.setBackground(Color.WHITE);
        addLabeledField(formPanel, gbc, "Type", typeCombo, 5);

        imagePathField = createStyledField();
        imagePathField.setEditable(false);
        browseButton = createStyledButton("Browse");
        JPanel imagePanel = new JPanel(new BorderLayout(8, 0));
        imagePanel.setOpaque(false);
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(browseButton, BorderLayout.EAST);
        addLabeledField(formPanel, gbc, "Image", imagePanel, 6);

        if (isEditMode && item != null) {
            nameField.setText(item.getName());
            descriptionArea.setText(item.getDescription());
            priceField.setText(String.valueOf(item.getPrice()));
            typeCombo.setSelectedItem(item.getType());
            imagePathField.setText(item.getImagePath() != null ? item.getImagePath() : "");
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        okButton = createStyledButton(isEditMode ? "Save" : "Add");
        cancelButton = createStyledButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        bgPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(bgPanel);

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> {
            result = null;
            dispose();
        });
        browseButton.addActionListener(e -> onBrowseImage());
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        styleField(field);
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

    private void styleTextArea(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(255, 255, 255, 230));
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
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

    private void onBrowseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Image");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            imagePathField.setText(file.getAbsolutePath());
        }
    }

    private void onOK() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String priceStr = priceField.getText().trim();
        String imagePath = imagePathField.getText().trim();
        MenuItem.Type type = (MenuItem.Type) typeCombo.getSelectedItem();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || type == null) {
            showError("All fields except image are required.");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Price must be a positive number.");
            return;
        }
        // For image, store as relative path if possible
        if (!imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                // Optionally, copy to resources/images/menu-icons/ and store relative path
                // For now, just store absolute path
                imagePath = imgFile.getAbsolutePath();
            } else {
                showError("Image file does not exist.");
                return;
            }
        } else {
            imagePath = null;
        }
        result = new MenuItem(name, description, price, type, imagePath);
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public MenuItem getResult() {
        return result;
    }
} 