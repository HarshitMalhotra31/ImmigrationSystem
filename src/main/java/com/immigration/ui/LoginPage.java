package com.immigration.ui;

import com.immigration.dao.UserDAO;
import com.immigration.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPage extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton loginBtn, registerBtn, checkStatusBtn;
    private UserDAO userDAO;

    public LoginPage() {
        userDAO = new UserDAO();
        setTitle("Immigration Compliance System - Login");
        setSize(480, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // Header
        add(UITheme.createHeaderPanel("Immigration Compliance System"), BorderLayout.NORTH);

        // Center form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BG_CARD);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(25, 35, 25, 35)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Subtitle
        JLabel subtitle = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(subtitle, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        formPanel.add(UITheme.createFormLabel("Username / Email:"), gbc);
        loginField = UITheme.createStyledTextField(16);
        gbc.gridx = 1;
        formPanel.add(loginField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(UITheme.createFormLabel("Password:"), gbc);
        passwordField = new JPasswordField(16);
        passwordField.setFont(UITheme.FONT_LABEL);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(UITheme.createFormLabel("Role:"), gbc);
        roleCombo = new JComboBox<>(new String[]{"Officer", "Admin"});
        roleCombo.setFont(UITheme.FONT_LABEL);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 8, 6, 8);
        loginBtn = UITheme.createStyledButton("Login", UITheme.PRIMARY);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setPreferredSize(new Dimension(100, 42));
        formPanel.add(loginBtn, gbc);

        // Visa Application button
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 8, 4, 8);
        registerBtn = UITheme.createStyledButton("Application for Visa (Traveler)", UITheme.ACCENT);
        registerBtn.setPreferredSize(new Dimension(100, 36));
        formPanel.add(registerBtn, gbc);

        // Check Status button
        gbc.gridy = 6;
        checkStatusBtn = UITheme.createStyledButton("Check Visa Status (Traveler)", UITheme.SUCCESS);
        checkStatusBtn.setPreferredSize(new Dimension(100, 36));
        formPanel.add(checkStatusBtn, gbc);

        // Wrap form in a centered container
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UITheme.BG_LIGHT);
        centerWrapper.setBorder(new EmptyBorder(20, 40, 20, 40));
        centerWrapper.add(formPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // Listeners
        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            new RegistrationForm().setVisible(true);
            dispose();
        });
        checkStatusBtn.addActionListener(e -> {
            new TravelerStatusPage().setVisible(true);
            dispose();
        });
    }

    private void handleLogin() {
        String input = loginField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (input.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both your ID and Password");
            return;
        }

        try {
            User user = null;
            if (role.equals("Admin")) {
                // Default Admin
                if (input.equalsIgnoreCase("admin") && password.equals("admin")) { 
                    new AdminDashboard().setVisible(true);
                    dispose();
                    return;
                }
                user = userDAO.findByUsername(input);
            } else if (role.equals("Officer")) {
                // Default Officer
                if (input.equalsIgnoreCase("officer") && password.equals("officer")) {
                    new OfficerDashboard().setVisible(true);
                    dispose();
                    return;
                }
                user = userDAO.findByUsername(input);
            }

            if (user != null && user.getRole().equalsIgnoreCase(role) && password.equals(user.getPassword())) {
                JOptionPane.showMessageDialog(this, "Welcome " + user.getName());
                // Navigate based on role if found in DB
                if (role.equals("Admin")) new AdminDashboard().setVisible(true);
                else new OfficerDashboard().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid ID, Password, or Unauthorized Role.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database.");
        }
    }
}
