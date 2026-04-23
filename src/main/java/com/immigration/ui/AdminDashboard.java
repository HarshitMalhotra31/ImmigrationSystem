package com.immigration.ui;

import com.immigration.dao.EntryExitDAO;
import com.immigration.dao.UserDAO;
import com.immigration.dao.VisaDAO;
import com.immigration.model.EntryExit;
import com.immigration.model.User;
import com.immigration.model.Visa;
import com.immigration.service.OverstayService;
import com.immigration.service.RiskService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;
    private VisaDAO visaDAO;
    private EntryExitDAO entryExitDAO;
    private OverstayService overstayService;
    private RiskService riskService;

    public AdminDashboard() {
        userDAO = new UserDAO();
        visaDAO = new VisaDAO();
        entryExitDAO = new EntryExitDAO();
        overstayService = new OverstayService();
        riskService = new RiskService();

        setTitle("Immigration Compliance System - Admin Reports");
        setSize(1150, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        // Header
        add(UITheme.createHeaderPanel("Admin Dashboard — Visa Compliance Reports"), BorderLayout.NORTH);

        // Table
        String[] columns = {"Email ID", "Name", "Passport No", "Nationality", "Phone Number", "Compliance Level", "Visa Status", "Overstay Days", "Fine"};
        tableModel = new DefaultTableModel(columns, 0);
        reportTable = new JTable(tableModel);
        UITheme.styleTable(reportTable);
        loadReportData();

        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(tableScroll, BorderLayout.CENTER);

        // Bottom buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.BLACK);
        JButton refreshBtn = UITheme.createStyledButton("Refresh Reports", UITheme.ACCENT);
        refreshBtn.setPreferredSize(new Dimension(180, 38));
        JButton logoutBtn = UITheme.createStyledButton("Logout", UITheme.DANGER);
        logoutBtn.setPreferredSize(new Dimension(120, 38));
        btnPanel.add(refreshBtn);
        btnPanel.add(logoutBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadReportData());
        logoutBtn.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
    }

    private void loadReportData() {
        tableModel.setRowCount(0);
        try {
            List<User> travelers = userDAO.getAllUsers();
            if (travelers == null || travelers.isEmpty()) {
                System.out.println("No user data found in the database.");
                return;
            }

            for (User user : travelers) {
                // Ensure role is checked safely
                String role = user.getRole();
                if (role != null && role.equalsIgnoreCase("Traveler")) {
                    Visa visa = visaDAO.findByUserId(user.getId());
                    EntryExit entryExit = entryExitDAO.findByUserId(user.getId());

                    String riskLevel = riskService.getRiskLevel(user.getRiskScore());
                    String visaStatus = (visa != null) ? visa.getApprovalStatus() : "N/A";
                    long overstayDays = 0;
                    double fine = 0.0;

                    if (visa != null && entryExit != null && entryExit.getExitDate() == null) {
                        overstayDays = overstayService.calculateOverstayDays(entryExit, visa);
                        fine = overstayService.calculateFine(overstayDays);
                    }

                    tableModel.addRow(new Object[]{
                            user.getEmail(),
                            user.getName(),
                            user.getPassportNumber() != null ? user.getPassportNumber() : "N/A",
                            user.getNationality(),
                            user.getPhoneNumber(),
                            riskLevel,
                            visaStatus,
                            (overstayDays > 0 ? overstayDays : "NONE"),
                            "$" + fine
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading report data. Please check database connection.");
        }
    }
}
