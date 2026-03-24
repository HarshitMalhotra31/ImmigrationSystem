package business;

import model.User;

public class RoleBasedAccessControl {
    public static boolean canAccess(User user, String feature) {
        if (user == null) return false;
        if ("ADMIN".equals(user.getRole())) return true;
        
        switch (feature) {
            case "DATA_ENTRY":
                return "USER".equals(user.getRole());
            case "REPORTS":
                return true; // Both can view reports
            default:
                return false;
        }
    }
}
