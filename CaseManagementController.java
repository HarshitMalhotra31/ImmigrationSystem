package controller;

import business.CaseStatusManager;

public class CaseManagementController {
    private CaseStatusManager statusManager = new CaseStatusManager();

    public String handleCaseAction(String currentStatus, String action) {
        return statusManager.updateStatus(currentStatus, action);
    }

    public boolean updateCaseStatus(String targetCaseId, String action) {
        String filePath = "c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\cases.txt";
        java.util.List<String> lines = new java.util.ArrayList<>();
        boolean found = false;

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts[0].equalsIgnoreCase(targetCaseId)) {
                    String newStatus = statusManager.updateStatus(parts[3], action);
                    // Rebuild line with the same data but updated status
                    String updatedLine = parts[0] + "|" + parts[1] + "|" + parts[2] + "|" + newStatus + "|" + parts[4];
                    if (parts.length > 5) {
                        for (int i = 5; i < parts.length; i++) updatedLine += "|" + parts[i];
                    }
                    line = updatedLine;
                    found = true;
                    
                    if ("APPROVE".equals(action)) {
                       
                        if (parts.length >= 9) {
                            addToTracking(parts[1], parts[5], parts[6], parts[7]);
                        } else {
                            addToTracking(parts[1], "N/A", java.time.LocalDate.now().plusMonths(3).toString(), "N/A");
                        }
                    }
                }
                lines.add(line);
            }
        } catch (java.io.IOException e) {
            System.err.println("Error reading cases: " + e.getMessage());
            return false;
        }

        if (found) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filePath))) {
                for (String line : lines) {
                    writer.println(line);
                }
                return true;
            } catch (java.io.IOException e) {
                System.err.println("Error writing cases: " + e.getMessage());
            }
        }
        return false;
    }

    private void addToTracking(String name, String type, String expiry, String passport) {
        String trackFile = "c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\accepted_applicants.txt";
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(trackFile, true)))) {
            out.println(name + "|" + type + "|" + expiry + "|" + passport);
        } catch (java.io.IOException e) {
            System.err.println("Error adding to tracking: " + e.getMessage());
        }
    }
}
