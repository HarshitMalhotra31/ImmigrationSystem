package business;

public class ComplianceRuleProcessor {
    public boolean checkCompliance(String applicantName, int age) {
        // Example rule: applicant must be over 18
        return age >= 18;
    }
}
