package com.immigration.util;

import com.immigration.dao.UserDAO;
import com.immigration.model.User;

public class DatabaseSetup {
    public static void main(String[] args) {
        System.out.println("Initializing Default User Accounts...");
        UserDAO userDAO = new UserDAO();

        try {
            // Add Admin account if it doesn't exist
            if (userDAO.findByUsername("admin") == null) {
                User admin = new User("System Administrator", "admin@immigration.com", "admin", "000", "Global", "Admin", "admin");
                userDAO.save(admin);
                System.out.println("PASS: Created Admin Account (User: admin / Pass: admin)");
            } else {
                System.out.println("SKIP: Admin account already exists.");
            }

            // Add Officer account if it doesn't exist
            if (userDAO.findByUsername("officer") == null) {
                User officer = new User("Immigration Officer", "officer@immigration.com", "officer", "111", "Global", "Officer", "officer");
                userDAO.save(officer);
                System.out.println("PASS: Created Officer Account (User: officer / Pass: officer)");
            } else {
                System.out.println("SKIP: Officer account already exists.");
            }

            System.out.println("\nDatabase initialization complete! You can now log in.");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
