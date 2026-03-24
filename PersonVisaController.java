package controller;

import business.VisaValidationEngine;
import business.ComplianceRuleProcessor;
import model.Case;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PersonVisaController {
    private VisaValidationEngine visaEngine = new VisaValidationEngine();
    private ComplianceRuleProcessor complianceProcessor = new ComplianceRuleProcessor();
    private static final String CASE_FILE = "c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\cases.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean processEntry(String name, int age, String visaType, String visaExpiry, String passportNo, String passportExpiry) {
        System.out.println("\n[SYSTEM DETECTION] Checking rules against: " + LocalDateTime.now().format(formatter));

        if (!complianceProcessor.checkCompliance(name, age)) {
            triggerAutomaticCase(name, "Compliance Failure: Underage applicant (Age: " + age + ")", visaType, visaExpiry, passportNo, passportExpiry);
            return false;
        }
        
        String error = visaEngine.validateVisaDetailed(visaType, visaExpiry, passportNo, passportExpiry);
        if (error != null) {
            System.out.println("\n!!! WARNING !!! " + error);
            triggerAutomaticCase(name, error, visaType, visaExpiry, passportNo, passportExpiry);
            return false;
        }

        saveToAccepted(name, visaType, visaExpiry, passportNo);
        return true;
    }

    private void saveToAccepted(String name, String visaType, String visaExpiry, String passportNo) {
        String file = "c:\\Users\\ishma\\Desktop\\javas\\ImmigrationSystem\\data\\accepted_applicants.txt";
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            out.println(name + "|" + visaType + "|" + visaExpiry + "|" + passportNo);
        } catch (IOException e) {
            System.err.println("Error saving accepted applicant: " + e.getMessage());
        }
    }

    private void triggerAutomaticCase(String name, String reason, String visaType, String visaExpiry, String passportNo, String passportExpiry) {
        Case newCase = new Case(name, reason, visaType, visaExpiry, passportNo, passportExpiry);
        System.out.println("SYSTEM: Creating automatic case -> " + newCase.getCaseId());
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(CASE_FILE, true)))) {
            out.println(newCase.toString());
        } catch (IOException e) {
            System.err.println("Error saving case: " + e.getMessage());
        }
    }
}
