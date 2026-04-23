package com.immigration;

import com.immigration.ui.LoginPage;
import com.immigration.ui.UITheme;

import javax.swing.*;

public class MainApplication {
    public static void main(String[] args) {
        // Run on Event Dispatch Thread (EDT) for Swing thread safety
        SwingUtilities.invokeLater(() -> {
            // Apply custom theme for a polished look
            UITheme.apply();

            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
            System.out.println("Immigration AI System Started Successfully.");
        });
    }
}
