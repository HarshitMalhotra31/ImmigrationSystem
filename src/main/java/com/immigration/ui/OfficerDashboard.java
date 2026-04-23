package com.immigration.ui;

import com.immigration.dao.EntryExitDAO;
import com.immigration.dao.UserDAO;
import com.immigration.dao.VisaDAO;
import com.immigration.model.EntryExit;
import com.immigration.model.User;
import com.immigration.model.Visa;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class OfficerDashboard extends JFrame {
    private JTable travelerTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;
    private VisaDAO visaDAO;
    private EntryExitDAO entryExitDAO;

    public OfficerDashboard() {
        userDAO = new UserDAO();
        visaDAO = new VisaDAO();
        entryExitDAO = new EntryExitDAO();

        setTitle("Officer Control Panel");
        setSize(1100, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        // Header
        add(UITheme.createHeaderPanel("Officer Control Panel"), BorderLayout.NORTH);

        // Table
        String[] columns = {"Email ID", "Name", "Passport No", "Nationality", "Phone Number", "Compliance Score", "Visa Status"};
        tableModel = new DefaultTableModel(columns, 0);
        travelerTable = new JTable(tableModel);
        UITheme.styleTable(travelerTable);
        loadTravelerData();

        JScrollPane tableScroll = new JScrollPane(travelerTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 0));
        add(tableScroll, BorderLayout.CENTER);

        // Sidebar
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        actionPanel.setBackground(Color.BLACK);

        JButton entryBtn = UITheme.createStyledButton("Record Entry", new Color(46, 204, 113));
        JButton exitBtn = UITheme.createStyledButton("Record Exit", new Color(230, 126, 34));
        JButton refreshBtn = UITheme.createStyledButton("Refresh List", UITheme.ACCENT);

        JButton approveBtn = UITheme.createStyledButton("Approve Visa", UITheme.SUCCESS);
        JButton rejectBtn = UITheme.createStyledButton("Reject Visa", UITheme.DANGER);

        JButton viewPassportBtn = UITheme.createStyledButton("View Passport", new Color(41, 128, 185));
        JButton viewProfileBtn = UITheme.createStyledButton("View Full Profile", new Color(108, 117, 125));
        JButton logoutBtn = UITheme.createStyledButton("Logout", new Color(192, 57, 43));

        actionPanel.add(entryBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionPanel.add(exitBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionPanel.add(refreshBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        actionPanel.add(approveBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionPanel.add(rejectBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionPanel.add(viewPassportBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionPanel.add(viewProfileBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        actionPanel.add(logoutBtn);

        add(actionPanel, BorderLayout.EAST);

        entryBtn.addActionListener(e -> handleEntry());
        exitBtn.addActionListener(e -> handleExit());
        refreshBtn.addActionListener(e -> loadTravelerData());
        approveBtn.addActionListener(e -> handleStatusUpdate("Approved"));
        rejectBtn.addActionListener(e -> handleStatusUpdate("Rejected"));
        viewPassportBtn.addActionListener(e -> handleViewPassport());
        viewProfileBtn.addActionListener(e -> handleViewProfile());
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }


    private void loadTravelerData() {
        tableModel.setRowCount(0);
        try {
            List<User> travelers = userDAO.getAllUsers();
            if (travelers == null || travelers.isEmpty()) {
                System.out.println("No traveler data found.");
                return;
            }

            for (User user : travelers) {
                String role = user.getRole();
                if (role != null && role.equalsIgnoreCase("Traveler")) {
                    Visa visa = visaDAO.findByUserId(user.getId());
                    String visaStatus = (visa != null) ? visa.getApprovalStatus() : "No Application";
                    tableModel.addRow(new Object[]{
                            user.getEmail(),
                            user.getName(),
                            user.getPassportNumber() != null ? user.getPassportNumber() : "N/A",
                            user.getNationality(),
                            user.getPhoneNumber(),
                            user.getRiskScore(),
                            visaStatus
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading traveler data. Please check database connection.");
        }
    }

    private void handleStatusUpdate(String newStatus) {
        int row = travelerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a traveler from the list.");
            return;
        }

        try {
            String email = (String) tableModel.getValueAt(row, 0);
            User user = userDAO.findByEmail(email);
            if (user == null) return;

            Visa visa = visaDAO.findByUserId(user.getId());
            if (visa == null) {
                JOptionPane.showMessageDialog(this, "No visa application found for this user.");
                return;
            }

            // ONE-TIME ACTION CONSTRAINT: Only allow status change if currently 'Applied'
            String currentStatus = visa.getApprovalStatus();
            if (!currentStatus.equalsIgnoreCase("Applied")) {
                JOptionPane.showMessageDialog(this, "Action DENIED: This application has already been processed (Current Status: " + currentStatus + ").");
                return;
            }

            visaDAO.updateStatus(visa.getId(), newStatus);
            JOptionPane.showMessageDialog(this, "Visa application has been " + newStatus.toUpperCase() + " for " + user.getName());
            loadTravelerData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating status.");
        }
    }

    private void handleEntry() {
        int row = travelerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a traveler.");
            return;
        }

        try {
            String email = (String) tableModel.getValueAt(row, 0);
            User user = userDAO.findByEmail(email);
            if (user == null) return;
            
            EntryExit existing = entryExitDAO.findByUserId(user.getId());

            if (existing != null && existing.getExitDate() == null) {
                JOptionPane.showMessageDialog(this, "Traveler is already recorded as inside the country.");
                return;
            }

            // Check visa status - must be "Approved" to allow entry
            Visa visa = visaDAO.findByUserId(user.getId());
            if (visa == null) {
                JOptionPane.showMessageDialog(this, "No visa application found. Traveler must apply for a visa first.");
                return;
            }

            String visaStatus = visa.getApprovalStatus();
            if (visaStatus.equalsIgnoreCase("Exited")) {
                JOptionPane.showMessageDialog(this, "Traveler has previously exited. They must apply for a new visa before re-entry.");
                return;
            }
            if (!visaStatus.equalsIgnoreCase("Approved")) {
                JOptionPane.showMessageDialog(this, "Entry DENIED: Visa is not approved (Current Status: " + visaStatus + ").");
                return;
            }

            visaDAO.updateStatus(visa.getId(), "Entry Approved");

            EntryExit entry = new EntryExit(user.getId());
            entryExitDAO.save(entry);

            JOptionPane.showMessageDialog(this, "Entry Recorded for " + user.getName());
            loadTravelerData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error recording entry.");
        }
    }

    private void handleExit() {
        int row = travelerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a traveler.");
            return;
        }

        try {
            String email = (String) tableModel.getValueAt(row, 0);
            User user = userDAO.findByEmail(email);
            if (user == null) return;
            
            EntryExit entryExit = entryExitDAO.findByUserId(user.getId());

            if (entryExit == null || entryExit.getExitDate() != null) {
                JOptionPane.showMessageDialog(this, "Traveler is not recorded as inside the country.");
                return;
            }

            String exitDate = LocalDateTime.now().toString();
            entryExitDAO.updateExit(user.getId(), exitDate, false);

            // Mark visa as "Exited" so traveler must apply for a new visa before re-entry
            Visa visa = visaDAO.findByUserId(user.getId());
            if (visa != null) {
                visaDAO.updateStatus(visa.getId(), "Exited");
            }

            JOptionPane.showMessageDialog(this, "Exit Recorded for " + user.getName() + ". Traveler must apply for a new visa before re-entry.");
            loadTravelerData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error recording exit.");
        }
    }

    private void handleViewProfile() {
        int row = travelerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a traveler.");
            return;
        }

        try {
            String email = (String) tableModel.getValueAt(row, 0);
            User user = userDAO.findByEmail(email);
            if (user == null) return;

            Visa visa = visaDAO.findByUserId(user.getId());
            
            StringBuilder profile = new StringBuilder();
            profile.append("--- Traveler Profile ---\n");
            profile.append("Name: ").append(user.getName()).append("\n");
            profile.append("Email: ").append(user.getEmail()).append("\n");
            profile.append("Nationality: ").append(user.getNationality()).append("\n");
            profile.append("Phone: ").append(user.getPhoneNumber()).append("\n");
            profile.append("Passport No: ").append(user.getPassportNumber() != null ? user.getPassportNumber() : "N/A").append("\n");
            profile.append("Status: ").append(user.getStatus()).append("\n");
            profile.append("Compliance Score: ").append(user.getRiskScore()).append("\n\n");
            
            if (visa != null) {
                profile.append("--- Visa Details ---\n");
                profile.append("Visa Status: ").append(visa.getApprovalStatus()).append("\n");
                profile.append("Days Requested: ").append(visa.getVisaDays()).append("\n");
                profile.append("Passport Expiry: ").append(visa.getPassportExpiry()).append("\n");
                profile.append("Visa Expiry: ").append(visa.getVisaExpiry()).append("\n");
            } else {
                profile.append("No active visa application found.\n");
            }

            JOptionPane.showMessageDialog(this, profile.toString(), "Traveler Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading profile details.");
        }
    }

    private void handleViewPassport() {
        int row = travelerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a traveler.");
            return;
        }

        try {
            String email = (String) tableModel.getValueAt(row, 0);
            User user = userDAO.findByEmail(email);
            if (user == null) return;

            String imagePath = user.getPassportImagePath();
            if (imagePath == null || imagePath.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No passport image uploaded for this traveler.",
                        "No Image", JOptionPane.WARNING_MESSAGE);
                return;
            }

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                JOptionPane.showMessageDialog(this,
                        "Passport image file not found at:\n" + imagePath,
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BufferedImage img = ImageIO.read(imageFile);
            if (img == null) {
                JOptionPane.showMessageDialog(this, "Could not read image file.");
                return;
            }

            // Scale image to fit a reasonable dialog size
            int maxWidth = 500;
            int maxHeight = 600;
            int origW = img.getWidth();
            int origH = img.getHeight();
            double scale = Math.min((double) maxWidth / origW, (double) maxHeight / origH);
            if (scale > 1.0) scale = 1.0; // don't upscale
            int newW = (int) (origW * scale);
            int newH = (int) (origH * scale);

            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);

            JLabel imgLabel = new JLabel(icon);
            imgLabel.setBorder(BorderFactory.createTitledBorder(
                    "Passport Image — " + user.getName() + " (" + email + ")"));

            JScrollPane scrollPane = new JScrollPane(imgLabel);
            scrollPane.setPreferredSize(new Dimension(newW + 40, newH + 60));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Passport Review — " + user.getName(),
                    JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading passport image.");
        }
    }
}
