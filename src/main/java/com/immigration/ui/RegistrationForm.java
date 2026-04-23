package com.immigration.ui;

import com.immigration.dao.UserDAO;
import com.immigration.dao.VisaDAO;
import com.immigration.model.User;
import com.immigration.model.Visa;
import com.immigration.service.RiskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class RegistrationForm extends JFrame {
    private JTextField nameField, emailField, phoneField, nationalityField, passportNumberField, visaDaysField;
    private JTextField passportExpiryField, visaExpiryField;
    private JLabel passportImageLabel;
    private JButton uploadBtn, submitBtn;
    private File selectedPassportImage;
    private UserDAO userDAO;
    private VisaDAO visaDAO;
    private RiskService riskService;

    public RegistrationForm() {
        userDAO = new UserDAO();
        visaDAO = new VisaDAO();
        riskService = new RiskService();

        setTitle("Immigration System - Visa Application Form");
        setSize(520, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // Header
        add(UITheme.createHeaderPanel("Visa Application Form"), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UITheme.BG_CARD);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                new EmptyBorder(20, 30, 20, 30)));

        JPanel wrapPanel = new JPanel(new GridBagLayout());
        wrapPanel.setBackground(UITheme.BG_LIGHT);
        wrapPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        wrapPanel.add(mainPanel);
        add(new JScrollPane(wrapPanel), BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Full Name
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(UITheme.createFormLabel("Full Name:"), gbc);
        nameField = UITheme.createStyledTextField(18);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Email ID
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(UITheme.createFormLabel("Email ID:"), gbc);
        emailField = UITheme.createStyledTextField(18);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Phone Number
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(UITheme.createFormLabel("Phone Number:"), gbc);
        phoneField = UITheme.createStyledTextField(18);
        gbc.gridx = 1;
        mainPanel.add(phoneField, gbc);

        // Nationality
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(UITheme.createFormLabel("Nationality:"), gbc);
        nationalityField = UITheme.createStyledTextField(18);
        gbc.gridx = 1;
        mainPanel.add(nationalityField, gbc);

        // Passport Number
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(UITheme.createFormLabel("Passport Number:"), gbc);
        passportNumberField = UITheme.createStyledTextField(18);
        gbc.gridx = 1;
        mainPanel.add(passportNumberField, gbc);

        // Intended Stay
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(UITheme.createFormLabel("Intended Stay (Days):"), gbc);
        visaDaysField = UITheme.createStyledTextField("30", 18);
        gbc.gridx = 1;
        mainPanel.add(visaDaysField, gbc);

        // Passport Expiry
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(UITheme.createFormLabel("Passport Expiry (YYYY-MM-DD):"), gbc);
        passportExpiryField = UITheme.createStyledTextField("2030-01-01", 18);
        gbc.gridx = 1;
        mainPanel.add(passportExpiryField, gbc);

        // Visa Expiry
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(UITheme.createFormLabel("Visa Expiry (YYYY-MM-DD):"), gbc);
        visaExpiryField = UITheme.createStyledTextField("2026-01-01", 18);
        gbc.gridx = 1;
        mainPanel.add(visaExpiryField, gbc);

        // Passport Image Upload
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Passport Image:"), gbc);

        JPanel uploadPanel = new JPanel(new BorderLayout(5, 0));
        uploadBtn = new JButton("Choose File...");
        uploadBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        passportImageLabel = new JLabel("No file selected");
        passportImageLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        passportImageLabel.setForeground(Color.GRAY);
        uploadPanel.add(uploadBtn, BorderLayout.WEST);
        uploadPanel.add(passportImageLabel, BorderLayout.CENTER);
        gbc.gridx = 1;
        mainPanel.add(uploadPanel, gbc);

        // Submit Button
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 6, 6, 6);
        submitBtn = UITheme.createStyledButton("Submit Application", UITheme.SUCCESS);
        submitBtn.setPreferredSize(new Dimension(100, 42));
        mainPanel.add(submitBtn, gbc);

        // Back Button
        row++;
        gbc.gridy = row;
        gbc.insets = new Insets(6, 6, 6, 6);
        JButton backBtn = UITheme.createStyledButton("Back to Login", UITheme.TEXT_MUTED);
        backBtn.setPreferredSize(new Dimension(100, 36));
        mainPanel.add(backBtn, gbc);

        // === Action Listeners ===

        uploadBtn.addActionListener(e -> handlePassportUpload());

        submitBtn.addActionListener(e -> handleSubmitWithOTP());

        backBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }

    private void handlePassportUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Passport Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Image files (JPG, PNG, BMP)", "jpg", "jpeg", "png", "bmp"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPassportImage = fileChooser.getSelectedFile();
            passportImageLabel.setText(selectedPassportImage.getName());
            passportImageLabel.setForeground(new Color(0, 128, 0));
        }
    }

    private void handleSubmitWithOTP() {
        // --- Step 1: Validate all fields first ---
        String rejectionReason = validateFields();
        if (rejectionReason != null && rejectionReason.startsWith("EMPTY:")) {
            // Missing required field — don't proceed at all
            JOptionPane.showMessageDialog(this, rejectionReason.substring(6));
            return;
        }

        String phone = phoneField.getText().trim();

        // --- Step 2: OTP Verification (3 attempts allowed) ---
        JOptionPane.showMessageDialog(this,
                "An OTP has been sent to your phone number: " + maskPhone(phone)
                        + "\nPlease enter the OTP to verify your identity.",
                "OTP Verification", JOptionPane.INFORMATION_MESSAGE);

        int maxAttempts = 3;
        boolean otpVerified = false;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            String enteredOTP = JOptionPane.showInputDialog(this,
                    "Enter OTP (sent to " + maskPhone(phone) + "):",
                    "OTP Verification", JOptionPane.QUESTION_MESSAGE);

            if (enteredOTP == null) {
                // User cancelled
                return;
            }

            if (enteredOTP.trim().equals("1234")) {
                otpVerified = true;
                break;
            }

            int remaining = maxAttempts - attempt;
            if (remaining > 0) {
                JOptionPane.showMessageDialog(this,
                        "Incorrect OTP! " + remaining + " attempt(s) remaining.",
                        "OTP Failed", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (!otpVerified) {
            JOptionPane.showMessageDialog(this,
                    "OTP verification failed after 3 attempts.\nApplication cannot be submitted. Please try again later.",
                    "Blocked", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "OTP Verified Successfully!",
                "Verified", JOptionPane.INFORMATION_MESSAGE);

        // --- Step 3: Process the registration (auto-reject if validation errors) ---
        handleRegistration(rejectionReason);
    }

    /**
     * Validates all input fields.
     * Returns null if everything is valid.
     * Returns "EMPTY:message" if a required field is missing (blocks submission entirely).
     * Returns a rejection reason string if data is invalid (auto-reject).
     */
    private String validateFields() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String nationality = nationalityField.getText().trim();
        String passportNumber = passportNumberField.getText().trim();
        String pExpiry = passportExpiryField.getText().trim();
        String vExpiry = visaExpiryField.getText().trim();

        // Check for empty required fields
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                nationality.isEmpty() || passportNumber.isEmpty() ||
                pExpiry.isEmpty() || vExpiry.isEmpty()) {
            return "EMPTY:Please fill all required fields.";
        }

        if (selectedPassportImage == null) {
            return "EMPTY:Please upload your passport image.";
        }

        // Validate intended stay
        try {
            int days = Integer.parseInt(visaDaysField.getText().trim());
            if (days <= 0) {
                return "Intended stay must be a positive number of days.";
            }
        } catch (NumberFormatException e) {
            return "EMPTY:Invalid Intended Stay. Please enter a number.";
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Invalid email format: " + email;
        }

        // Validate phone number (must be digits only, 10 digits)
        if (!phone.matches("^\\d{10}$")) {
            return "Invalid phone number. Must be exactly 10 digits.";
        }

        // Validate passport number: 1 uppercase letter + 7 digits (e.g., A1234567)
        if (!passportNumber.matches("^[A-Z]\\d{7}$")) {
            return "Invalid passport number. Must be 1 uppercase letter followed by 7 digits (e.g., A1234567).";
        }

        // Passport number must be unique — it is the key to differentiate travelers
        if (userDAO.findByPassportNumber(passportNumber) != null) {
            return "EMPTY:A traveler with Passport Number " + passportNumber + " already exists. Passport number must be unique.";
        }

        // Validate date format YYYY-MM-DD
        String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!pExpiry.matches(dateRegex)) {
            return "Invalid passport expiry date format. Must be YYYY-MM-DD.";
        }
        if (!vExpiry.matches(dateRegex)) {
            return "Invalid visa expiry date format. Must be YYYY-MM-DD.";
        }

        // Visa expiry must not be after passport expiry
        if (vExpiry.compareTo(pExpiry) > 0) {
            return "Visa expiry date cannot be after passport expiry date.";
        }

        return null; // All valid
    }

    private void handleRegistration(String rejectionReason) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String nationality = nationalityField.getText().trim();
        String passportNumber = passportNumberField.getText().trim();
        String pExpiry = passportExpiryField.getText().trim();
        String vExpiry = visaExpiryField.getText().trim();
        int visaDays = Integer.parseInt(visaDaysField.getText().trim());

        boolean autoReject = (rejectionReason != null);
        String initialStatus = autoReject ? "Rejected (Auto: " + rejectionReason + ")" : "Applied";

        // Username is null for travelers as they use Email for ID
        User user = new User(name, email, null, phone, nationality, "Traveler", null);
        user.setPassportNumber(passportNumber);
        Visa visaTemplate = new Visa(null, visaDays, pExpiry, vExpiry);
        visaTemplate.setApprovalStatus(initialStatus);

        int score = riskService.calculateRiskScore(user, visaTemplate);
        user.setRiskScore(score);

        try {
            // Copy passport image to project folder
            String savedImagePath = savePassportImage(email);
            user.setPassportImagePath(savedImagePath);

            userDAO.save(user);
            User savedUser = userDAO.findByEmail(email);

            if (savedUser == null) {
                JOptionPane.showMessageDialog(this, "Error: Could not retrieve saved user.");
                return;
            }

            Visa newVisa = new Visa(savedUser.getId(), visaDays, pExpiry, vExpiry);
            newVisa.setApprovalStatus(initialStatus);
            visaDAO.save(newVisa);

            if (autoReject) {
                JOptionPane.showMessageDialog(this,
                        "Application AUTOMATICALLY REJECTED!\nReason: " + rejectionReason,
                        "Auto-Rejected", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Your Visa Application has been Submitted Successfully!\n"
                                + "Email ID: " + email + "\n"
                                + "Passport image uploaded for officer review.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            new LoginPage().setVisible(true);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database.");
        }
    }

    /**
     * Copies the selected passport image to the project's passport_images/ directory.
     * Returns the absolute path of the saved copy.
     */
    private String savePassportImage(String email) throws IOException {
        Path destDir = Paths.get("passport_images");
        if (!Files.exists(destDir)) {
            Files.createDirectories(destDir);
        }

        // Build a unique filename using email
        String originalName = selectedPassportImage.getName();
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.'))
                : ".jpg";
        String safeEmail = email.replaceAll("[^a-zA-Z0-9]", "_");
        String destFileName = "passport_" + safeEmail + extension;

        Path destPath = destDir.resolve(destFileName);
        Files.copy(selectedPassportImage.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

        return destPath.toAbsolutePath().toString();
    }

    /**
     * Masks a phone number for display: 91XXXX3456
     */
    private String maskPhone(String phone) {
        if (phone.length() <= 4) return phone;
        return phone.substring(0, 2) + "XXXX" + phone.substring(phone.length() - 4);
    }
}
