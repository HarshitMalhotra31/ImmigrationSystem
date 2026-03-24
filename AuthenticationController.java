package controller;

import model.User;
import java.io.*;
import java.util.*;

public class AuthenticationController {
    private static final String FILE_PATH = "c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\users.txt";

    public User login(String username, String password) {
        return loginWithRole(username, password, null);
    }

    public User loginWithRole(String username, String password, String expectedRole) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    String role = parts[2];
                    if (expectedRole == null || expectedRole.equals(role)) {
                        return new User(parts[0], parts[1], role);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
        }
        return null;
    }

    public boolean signup(String username, String password) {
        if (isUsernameTaken(username)) return false;
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            writer.println(username + "," + password + ",USER");
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to users file: " + e.getMessage());
            return false;
        }
    }

    private boolean isUsernameTaken(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.split(",")[0].equals(username)) return true;
            }
        } catch (IOException e) {}
        return false;
    }
}
