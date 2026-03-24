package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLoggingController {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logAction(String username, String action) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[AUDIT LOG][" + timestamp + "] User: " + username + " -> Action: " + action);
    }
}
