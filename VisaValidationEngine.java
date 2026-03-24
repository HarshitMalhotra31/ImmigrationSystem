package business;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;

public class VisaValidationEngine {
    public String validateVisaDetailed(String visaType, String expiryDate, String passportNumber, String passportExpiry) {
        try {
            LocalDate visaExp = LocalDate.parse(expiryDate);
            LocalDate passExp = LocalDate.parse(passportExpiry);
            LocalDate today = LocalDate.now();

            if (visaExp.isBefore(today)) {
                return "CRITICAL: Visa already expired on " + visaExp;
            }
            if (passExp.isBefore(today)) {
                return "CRITICAL: Passport already expired on " + passExp;
            }

            long monthsBetween = ChronoUnit.MONTHS.between(visaExp, passExp);
            if (monthsBetween < 6) {
                return "POLICY_VIOLATION: Passport validity less than 6 months after visa expiry (Gap: " + monthsBetween + " months)";
            }

            if ("TOURIST".equalsIgnoreCase(visaType)) {
                long daysValid = ChronoUnit.DAYS.between(today, visaExp);
                if (daysValid > 180) {
                    return "DURATION_VIOLATION: Tourist visa exceeds 180-day limit (" + daysValid + " days)";
                }
            }

            if (!passportNumber.matches("^[A-Z0-9]{6,12}$")) {
                return "FORMAT_ERROR: Invalid Passport Number format";
            }

            return null; // Success - no error
        } catch (DateTimeParseException e) {
            return "FORMAT_ERROR: Invalid date format. Use YYYY-MM-DD";
        }
    }
}
