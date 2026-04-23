package com.immigration.model;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User {
    private ObjectId id;
    private String name;
    private String email;
    private String username; // For Admin/Officer
    private String phoneNumber;
    private String nationality;
    private String role; // "Traveler", "Officer", "Admin"
    private int riskScore;
    private String status; // "Active", "Blacklisted"
    private String password;
    private String passportImagePath; // File path to uploaded passport image
    private String passportNumber; // Passport number (e.g., A1234567)

    public User(String name, String email, String username, String phoneNumber, String nationality, String role, String password) {
        this.id = new ObjectId();
        this.name = name;
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.nationality = nationality;
        this.role = role;
        this.riskScore = 0;
        this.status = "Active";
        this.password = password;
    }

    public User(Document doc) {
        this.id = doc.getObjectId("_id");
        this.name = doc.getString("name");
        this.email = doc.getString("email");
        this.username = doc.getString("username");
        this.phoneNumber = doc.getString("phoneNumber");
        this.nationality = doc.getString("nationality");
        this.role = doc.getString("role");
        this.riskScore = doc.getInteger("riskScore", 0);
        this.status = doc.getString("status");
        this.password = doc.getString("password");
        this.passportImagePath = doc.getString("passportImagePath");
        this.passportNumber = doc.getString("passportNumber");
    }

    public Document toDocument() {
        Document doc = new Document()
            .append("name", name)
            .append("email", email)
            .append("username", username)
            .append("phoneNumber", phoneNumber)
            .append("nationality", nationality)
            .append("role", role)
            .append("riskScore", riskScore)
            .append("status", status)
            .append("password", password)
            .append("passportImagePath", passportImagePath)
            .append("passportNumber", passportNumber);
        if (id != null) doc.append("_id", id);
        return doc;
    }

    // Getters and Setters
    public ObjectId getId() { return id ; }
    public void setId(ObjectId id) { this.id = id; }
    public String getName() { return name ; }
    public String getEmail() { return email ; }
    public String getUsername() { return username ; }
    public String getPhoneNumber() { return phoneNumber ; }
    public String getNationality() { return nationality ; }
    public String getRole() { return role ; }
    public int getRiskScore() { return riskScore ; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore ; }
    public String getStatus() { return status ; }
    public void setStatus(String status) { this.status = status ; }
    public String getPassword() { return password ; }
    public void setPassword(String password) { this.password = password ; }
    public String getPassportImagePath() { return passportImagePath ; }
    public void setPassportImagePath(String passportImagePath) { this.passportImagePath = passportImagePath ; }
    public String getPassportNumber() { return passportNumber ; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber ; }
}
