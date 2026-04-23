package com.immigration.ui;

import com.immigration.dao.EntryExitDAO;
import com.immigration.dao.UserDAO;
import com.immigration.dao.VisaDAO;
import com.immigration.model.EntryExit;
import com.immigration.model.User;
import com.immigration.model.Visa;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TravelerStatusPage extends JFrame {
    private JTextField emailField;
    private JPanel resultPanel;
    private UserDAO userDAO;
    private VisaDAO visaDAO;
    private EntryExitDAO entryExitDAO;

    public TravelerStatusPage() {
        userDAO = new UserDAO();
        visaDAO = new VisaDAO();
        entryExitDAO = new EntryExitDAO();

        setTitle("Immigration System - Check Visa Status");
        setSize(550, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === Top Panel: Title + Search ===
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(20, 25, 10, 25));

        JLabel titleLabel = new JLabel("Check Your Visa Status");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel subtitleLabel = new JLabel("Enter your registered Email ID to view your application status");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(subtitleLabel);

        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.add(new JLabel("Email ID:"));
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(emailField);

        JButton checkBtn = new JButton("Check Status");
        checkBtn.setFont(new Font("Arial", Font.BOLD, 13));
        checkBtn.setBackground(new Color(52, 152, 219));
        checkBtn.setForeground(Color.BLACK);
        checkBtn.setFocusPainted(false);
        searchPanel.add(checkBtn);
        topPanel.add(searchPanel);

        add(topPanel, BorderLayout.NORTH);

        // === Center: Result Panel (scrollable) ===
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(new EmptyBorder(10, 25, 10, 25));

        JLabel placeholderLabel = new JLabel("Your status will appear here after lookup.", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        placeholderLabel.setForeground(Color.GRAY);
        placeholderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(placeholderLabel);

        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // === Bottom: Back Button ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(new EmptyBorder(5, 0, 15, 0));
        JButton backBtn = new JButton("Back to Login");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // === Listeners ===
        checkBtn.addActionListener(e -> handleCheckStatus());
        emailField.addActionListener(e -> handleCheckStatus()); // Enter key
        backBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }

    private void handleCheckStatus() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Email ID.");
            return;
        }

        resultPanel.removeAll();

        try {
            User user = userDAO.findByEmail(email);

            if (user == null) {
                showError("No traveler found with Email ID: " + email);
                return;
            }

            if (!"Traveler".equalsIgnoreCase(user.getRole())) {
                showError("This Email ID is not registered as a Traveler.");
                return;
            }

            // === OTP Verification (3 attempts allowed) ===
            String phone = user.getPhoneNumber() != null ? user.getPhoneNumber() : "Unknown";
            String maskedPhone = maskPhone(phone);

            JOptionPane.showMessageDialog(this,
                    "An OTP has been sent to your registered phone number: " + maskedPhone
                            + "\nPlease enter the OTP to verify your identity.",
                    "OTP Verification", JOptionPane.INFORMATION_MESSAGE);

            int maxAttempts = 3;
            boolean otpVerified = false;

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                String enteredOTP = JOptionPane.showInputDialog(this,
                        "Enter OTP (sent to " + maskedPhone + "):",
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
                        "OTP verification failed after 3 attempts.\nAccess blocked. Please try again later.",
                        "Blocked", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "OTP Verified Successfully!",
                    "Verified", JOptionPane.INFORMATION_MESSAGE);

            // === Traveler Info Card ===
            JPanel travelerCard = createCard("Traveler Information");
            addField(travelerCard, "Name", user.getName());
            addField(travelerCard, "Email", user.getEmail());
            addField(travelerCard, "Phone", user.getPhoneNumber());
            addField(travelerCard, "Nationality", user.getNationality());
            addField(travelerCard, "Passport No", user.getPassportNumber() != null ? user.getPassportNumber() : "N/A");
            addField(travelerCard, "Compliance Score", String.valueOf(user.getRiskScore()));
            addField(travelerCard, "Account Status", user.getStatus() != null ? user.getStatus() : "Active");
            resultPanel.add(travelerCard);
            resultPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // === Visa Status Card ===
            Visa visa = visaDAO.findByUserId(user.getId());
            JPanel visaCard = createCard("Visa Application Status");

            if (visa != null) {
                String status = visa.getApprovalStatus();
                addField(visaCard, "Application Date", formatDateTime(visa.getApplicationDate()));
                addStatusField(visaCard, "Current Status", status);
                addField(visaCard, "Intended Stay", visa.getVisaDays() + " days");
                addField(visaCard, "Passport Expiry", visa.getPassportExpiry());
                addField(visaCard, "Visa Expiry", visa.getVisaExpiry());

                // Show helpful message based on status
                String message = getStatusMessage(status);
                if (message != null) {
                    JLabel msgLabel = new JLabel("<html><i>" + message + "</i></html>");
                    msgLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                    msgLabel.setForeground(new Color(100, 100, 100));
                    msgLabel.setBorder(new EmptyBorder(8, 10, 5, 10));
                    visaCard.add(msgLabel);
                }
            } else {
                JLabel noVisaLabel = new JLabel("No visa application found.");
                noVisaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                noVisaLabel.setForeground(Color.RED);
                noVisaLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                visaCard.add(noVisaLabel);
            }

            resultPanel.add(visaCard);
            resultPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            // === Entry/Exit Record Card ===
            EntryExit entryExit = entryExitDAO.findByUserId(user.getId());
            JPanel entryExitCard = createCard("Entry / Exit Record");

            if (entryExit != null) {
                addField(entryExitCard, "Last Entry Date", formatDateTime(entryExit.getEntryDate()));
                if (entryExit.getExitDate() != null) {
                    addField(entryExitCard, "Exit Date", formatDateTime(entryExit.getExitDate()));
                    addStatusField(entryExitCard, "Current Location", "Outside the Country");
                } else {
                    addStatusField(entryExitCard, "Current Location", "Inside the Country");
                }
                addField(entryExitCard, "Overstay Flag", entryExit.isOverstayFlag() ? "YES — Overstay Detected" : "No");
            } else {
                JLabel noRecordLabel = new JLabel("No entry/exit records found.");
                noRecordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                noRecordLabel.setForeground(Color.GRAY);
                noRecordLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
                entryExitCard.add(noRecordLabel);
            }

            resultPanel.add(entryExitCard);

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error connecting to database. Please try again.");
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void showError(String message) {
        JLabel errorLabel = new JLabel(message);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setBorder(new EmptyBorder(30, 0, 0, 0));
        resultPanel.add(errorLabel);
        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(44, 62, 80)));
        card.setBackground(new Color(250, 250, 250));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    private void addField(JPanel card, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row.setOpaque(false);

        JLabel keyLabel = new JLabel(label + ":");
        keyLabel.setFont(new Font("Arial", Font.BOLD, 13));
        keyLabel.setPreferredSize(new Dimension(150, 20));
        row.add(keyLabel);

        JLabel valLabel = new JLabel(value != null ? value : "N/A");
        valLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        row.add(valLabel);

        card.add(row);
    }

    private void addStatusField(JPanel card, String label, String status) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row.setOpaque(false);

        JLabel keyLabel = new JLabel(label + ":");
        keyLabel.setFont(new Font("Arial", Font.BOLD, 13));
        keyLabel.setPreferredSize(new Dimension(150, 20));
        row.add(keyLabel);

        JLabel valLabel = new JLabel(status);
        valLabel.setFont(new Font("Arial", Font.BOLD, 13));
        valLabel.setForeground(getStatusColor(status));
        row.add(valLabel);

        card.add(row);
    }

    private Color getStatusColor(String status) {
        if (status == null) return Color.GRAY;
        String s = status.toLowerCase();
        if (s.contains("approved") || s.contains("inside")) return new Color(39, 174, 96);
        if (s.contains("rejected")) return new Color(231, 76, 60);
        if (s.contains("applied")) return new Color(41, 128, 185);
        if (s.contains("exited") || s.contains("outside")) return new Color(230, 126, 34);
        return Color.DARK_GRAY;
    }

    private String getStatusMessage(String status) {
        if (status == null) return null;
        String s = status.toLowerCase();
        if (s.equals("applied")) {
            return "Your application is under review. Please wait for officer approval.";
        } else if (s.equals("approved")) {
            return "Congratulations! Your visa has been approved. You may proceed to the immigration counter for entry.";
        } else if (s.contains("rejected")) {
            return "Your application has been rejected. Please apply again with correct documents.";
        } else if (s.equals("entry approved")) {
            return "You have been granted entry. Welcome to the country!";
        } else if (s.equals("exited")) {
            return "You have exited the country. You must apply for a new visa before re-entry.";
        }
        return null;
    }

    /**
     * Formats a LocalDateTime string to a more readable format.
     * Input:  2026-04-23T22:10:15.123
     * Output: 2026-04-23  22:10:15
     */
    private String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null) return "N/A";
        try {
            // Split on 'T' and trim fractional seconds
            String[] parts = dateTimeStr.split("T");
            if (parts.length == 2) {
                String time = parts[1];
                if (time.contains(".")) {
                    time = time.substring(0, time.indexOf('.'));
                }
                return parts[0] + "  " + time;
            }
        } catch (Exception e) {
            // ignore
        }
        return dateTimeStr;
    }

    /**
     * Masks a phone number for display: 91XXXX3456
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() <= 4) return phone != null ? phone : "****";
        return phone.substring(0, 2) + "XXXX" + phone.substring(phone.length() - 4);
    }
}
