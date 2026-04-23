package com.immigration.service;

import com.immigration.model.User;
import com.immigration.model.Visa;

import java.util.Arrays;
import java.util.List;

public class RiskService {
    private static final List<String> HIGH_RISK_COUNTRIES = Arrays.asList("None", "Unknown"); // Example

    public int calculateRiskScore(User user, Visa visa) {
        int score = 0;

        // Nationality-based scoring
        if (HIGH_RISK_COUNTRIES.contains(user.getNationality())) {
            score += 40;
        } else {
            score += 10;
        }

        // Validity logic
        if (visa.getVisaDays() > 90) {
            score += 20; // Longer stays might be higher risk for overstay
        } else {
            score += 5;
        }

        // Simulating "One-way ticket" risk
        // In a real system, we'd check actual flight data
        if (user.getEmail().startsWith("OW")) {
            score += 30; // One-way ticket simulation
        }

        return score;
    }

    public String getRiskLevel(int score) {
        if (score < 20) return "LOW";
        if (score < 50) return "MEDIUM";
        return "HIGH";
    }
}
