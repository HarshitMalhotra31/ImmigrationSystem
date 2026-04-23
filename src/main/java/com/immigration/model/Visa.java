package com.immigration.model;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;

public class Visa {
    private ObjectId id;
    private ObjectId userId;
    private String applicationDate;
    private String approvalStatus; // "Applied", "Approved", "Rejected"
    private int visaDays; // e.g., 30
    private String passportExpiry;
    private String visaExpiry;

    public Visa(ObjectId userId, int visaDays, String passportExpiry, String visaExpiry) {
        this.userId = userId;
        this.applicationDate = LocalDateTime.now().toString();
        this.approvalStatus = "Applied";
        this.visaDays = visaDays;
        this.passportExpiry = passportExpiry;
        this.visaExpiry = visaExpiry;
    }

    public Visa(Document doc) {
        this.id = doc.getObjectId("_id");
        this.userId = doc.getObjectId("userId");
        this.applicationDate = doc.getString("applicationDate");
        this.approvalStatus = doc.getString("approvalStatus");
        this.visaDays = doc.getInteger("visaDays", 30);
        this.passportExpiry = doc.getString("passportExpiry");
        this.visaExpiry = doc.getString("visaExpiry");
    }

    public Document toDocument() {
        return new Document("userId", userId)
                .append("applicationDate", applicationDate)
                .append("approvalStatus", approvalStatus)
                .append("visaDays", visaDays)
                .append("passportExpiry", passportExpiry)
                .append("visaExpiry", visaExpiry);
    }

    public ObjectId getId() { return id ; }
    public ObjectId getUserId() { return userId ; }
    public String getApprovalStatus() { return approvalStatus ; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus ; }
    public int getVisaDays() { return visaDays ; }
    public String getApplicationDate() { return applicationDate ; }
    public String getPassportExpiry() { return passportExpiry ; }
    public String getVisaExpiry() { return visaExpiry ; }
}
