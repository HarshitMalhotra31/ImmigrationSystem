package business;

public class CaseStatusManager {
    public String updateStatus(String currentStatus, String action) {
        if ("SUBMIT".equals(action)) return "PENDING";
        if ("APPROVE".equals(action)) return "APPROVED";
        if ("REJECT".equals(action)) return "REJECTED";
        return currentStatus;
    }
}
