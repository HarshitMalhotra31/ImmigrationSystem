import controller.*;
import model.User;
import java.util.Scanner;

public class Main {
    private static AuthenticationController authController = new AuthenticationController();
    private static PersonVisaController personVisaController = new PersonVisaController();
    private static CaseManagementController caseController = new CaseManagementController();
    private static AuditLoggingController auditController = new AuditLoggingController();
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== WELCOME TO IMMIGRATION SYSTEM  ===");
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showAuthMenu() {
        System.out.println("\n--- ACCESS PORTAL ---");
        System.out.println("1. Admin Login");
        System.out.println("2. User Login");
        System.out.println("3. User Signup");
        System.out.println("4. Exit");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();

        if ("1".equals(choice)) {
            handleLogin("ADMIN");
        } else if ("2".equals(choice)) {
            handleLogin("USER");
        } else if ("3".equals(choice)) {
            System.out.print("New Username: ");
            String username = scanner.nextLine();
            System.out.print("New Password: ");
            String password = scanner.nextLine();
            if (authController.signup(username, password)) {
                System.out.println("Signup successful! You can now login.");
            } else {
                System.out.println("Username already taken or error occurred.");
            }
        } else if ("4".equals(choice)) {
            System.exit(0);
        }
    }

    private static void handleLogin(String role) {
        System.out.println("\n--- " + role + " LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        currentUser = authController.loginWithRole(username, password, role);
        if (currentUser != null) {
            System.out.println("Login successful! Welcome " + currentUser.getUsername());
            auditController.logAction(currentUser.getUsername(), "LOGIN_" + role);
        } else {
            System.out.println("Invalid credentials or incorrect role mapping.");
        }
    }

    private static void showMainMenu() {
        if ("ADMIN".equals(currentUser.getRole())) {
            showAdminMenu();
        } else {
            showUserMenu();
        }
    }

    private static void showAdminMenu() {
        System.out.println("\n--- ADMIN CONTROL PANEL ---");
        System.out.println("1. View Reports");
        System.out.println("2. Case Management Console");
        System.out.println("3. View System-Generated Cases");
        System.out.println("4. View All Users");
        System.out.println("5. View Accepted Tracking (Stay Duration)");
        System.out.println("6. Logout");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": viewAdminReports(); break;
            case "2": handleCaseManagement(); break;
            case "3": viewGeneratedCases(); break;
            case "4": viewAllUsers(); break;
            case "5": viewAcceptedTracking(); break;
            case "6": 
                auditController.logAction(currentUser.getUsername(), "LOGOUT");
                currentUser = null; 
                break;
            default: System.out.println("Invalid option.");
        }
    }

    private static void viewAllUsers() {
        System.out.println("\n--- REGISTERED USERS ---");
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                String[] p = line.split(",");
                if (p.length >= 3) {
                    System.out.printf("User: %-15s | Role: %s%n", p[0], p[2]);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Error reading users file.");
        }
    }

    private static void viewAdminReports() {
        System.out.println("\n--- SYSTEM STATISTICS REPORT ---");
        int userCount = 0;
        int caseCount = 0;
        int openCases = 0;

        try {
            java.io.BufferedReader uBr = new java.io.BufferedReader(new java.io.FileReader("c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\users.txt"));
            while (uBr.readLine() != null) userCount++;
            uBr.close();

            java.io.BufferedReader cBr = new java.io.BufferedReader(new java.io.FileReader("c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\cases.txt"));
            String line;
            while ((line = cBr.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                caseCount++;
                if (line.contains("|OPEN|")) openCases++;
            }
            cBr.close();

            System.out.println("Total Registered Users: " + userCount);
            System.out.println("Total Cases Generated: " + caseCount);
            System.out.println("Active/Open Cases:     " + openCases);
            auditController.logAction(currentUser.getUsername(), "VIEW_ADMIN_STATS");
        } catch (java.io.IOException e) {
            System.out.println("Error generating reports: " + e.getMessage());
        }
    }

    private static void showUserMenu() {
        System.out.println("\n--- USER DASHBOARD ---");
        System.out.println("1. Submit New Application (Data Entry)");
        System.out.println("2. View My Application Status");
        System.out.println("3. My Profile");
        System.out.println("4. Contact Support");
        System.out.println("5. Logout");
        System.out.print("Choose option: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": handleDataEntry(); break;
            case "2": viewUserApplicationStatus(); break;
            case "3": 
                System.out.println("\n--- MY PROFILE ---");
                System.out.println("Username: " + currentUser.getUsername());
                System.out.println("Role: " + currentUser.getRole());
                break;
            case "4": 
                System.out.println("\n--- CONTACT SUPPORT ---");
                System.out.println("Email: support@immigrationsystem.gov");
                System.out.println("Hotline: 1-800-IMM-INFO");
                break;
            case "5": 
                auditController.logAction(currentUser.getUsername(), "LOGOUT");
                currentUser = null; 
                break;
            default: System.out.println("Invalid option.");
        }
    }

    private static void viewUserApplicationStatus() {
        System.out.println("\n--- MY APPLICATION STATUS ---");
        boolean found = false;
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\cases.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length > 1 && p[1].equalsIgnoreCase(currentUser.getUsername())) {
                    System.out.printf("[%s] Status: %s | Issue: %s | Date: %s%n", p[0], p[3], p[2], p[4]);
                    found = true;
                }
            }
        } catch (java.io.IOException e) {}

        if (!found) {
            System.out.println("No pending cases or violations found for your account.");
        }
    }

    private static void viewAcceptedTracking() {
        String file = "c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\accepted_applicants.txt";
        System.out.println("\n--- ACCEPTED APPLICANT TRACKING (STAY DURATION) ---");
        java.util.List<String> records = new java.util.ArrayList<>();
        
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            int i = 1;
            java.time.LocalDate today = java.time.LocalDate.now();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length >= 3) {
                    try {
                        java.time.LocalDate expiry = java.time.LocalDate.parse(p[2]);
                        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, expiry);
                        System.out.printf("%d. %-15s | Visa: %-8s | Days Remaining: %d days%n", i++, p[0], p[1], daysLeft);
                        records.add(line);
                    } catch (java.time.format.DateTimeParseException e) {
                        System.out.printf("%d. %-15s | Visa: %-8s | [DATE ERROR: %s]%n", i++, p[0], p[1], p[2]);
                        records.add(line);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("No accepted applicants found or error reading file.");
            return;
        }

        if (records.isEmpty()) return;

        System.out.print("\nEnter index to mark as 'DEPARTED' (or 0 to go back): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice > 0 && choice <= records.size()) {
                handleDeparture(records, choice - 1);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private static void handleDeparture(java.util.List<String> records, int indexToRemove) {
        String file = "c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\accepted_applicants.txt";
        String removedRecord = records.remove(indexToRemove);
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            for (String record : records) {
                writer.println(record);
            }
            System.out.println("SUCCESS: Applicant " + removedRecord.split("\\|")[0] + " marked as departed and removed from tracking.");
            auditController.logAction(currentUser.getUsername(), "MARK_DEPARTED_" + removedRecord.split("\\|")[0]);
        } catch (java.io.IOException e) {
            System.out.println("Error updating tracking file: " + e.getMessage());
        }
    }

    private static void viewGeneratedCases() {
        System.out.println("\n--- SYSTEM-GENERATED AUTO-CASES ---");
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\cases.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if (p.length >= 9) {
                    System.out.printf("[%s] Applicant: %-10s | Info: %s, %s | Status: %-10s | Issue: %s%n", p[0], p[1], p[5], p[7], p[3], p[2]);
                } else if (p.length >= 5) {
                    System.out.printf("[%s] Applicant: %-10s | Status: %-10s | Issue: %s%n", p[0], p[1], p[3], p[2]);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("No cases found or error reading file.");
        }
    }

    private static void handleDataEntry() {
        try {
            System.out.print("Applicant Name: ");
            String name = scanner.nextLine();
            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());
            System.out.print("Visa Type (e.g., TOURIST, WORK): ");
            String visa = scanner.nextLine();
            System.out.print("Visa Expiry (YYYY-MM-DD): ");
            String vExpiry = scanner.nextLine();
            System.out.print("Passport Number: ");
            String pNo = scanner.nextLine();
            System.out.print("Passport Expiry (YYYY-MM-DD): ");
            String pExpiry = scanner.nextLine();

            if (personVisaController.processEntry(name, age, visa, vExpiry, pNo, pExpiry)) {
                System.out.println("\nSUCCESS: Data entry validated and submitted automatically.");
                auditController.logAction(currentUser.getUsername(), "DATA_ENTRY_SUBMIT");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Age must be a number.");
        }
    }

    private static void handleCaseManagement() {
        System.out.println("\n--- ADMIN CASE MANAGEMENT CONSOLE ---");
        viewGeneratedCases(); // Show cases first
        
        System.out.print("\nEnter Case ID to manage (e.g., CASE-1234): ");
        String caseId = scanner.nextLine();
        System.out.print("Action (APPROVE/REJECT/SUBMIT): ");
        String action = scanner.nextLine();
        
        if (caseController.updateCaseStatus(caseId, action)) {
            System.out.println("SUCCESS: Case " + caseId + " status updated via " + action);
            auditController.logAction(currentUser.getUsername(), "ADMIN_UPDATE_CASE_" + caseId);
        } else {
            System.out.println("ERROR: Case ID not found or invalid action.");
        }
    }
}
