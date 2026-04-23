package com.immigration.model;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;

public class EntryExit {
    private ObjectId id;
    private ObjectId userId;
    private String entryDate;
    private String exitDate;
    private boolean overstayFlag;

    public EntryExit(ObjectId userId) {
        this.userId = userId;
        this.entryDate = LocalDateTime.now().toString();
        this.overstayFlag = false;
    }

    public EntryExit(Document doc) {
        this.id = doc.getObjectId("_id");
        this.userId = doc.getObjectId("userId");
        this.entryDate = doc.getString("entryDate");
        this.exitDate = doc.getString("exitDate");
        this.overstayFlag = doc.getBoolean("overstayFlag", false);
    }

    public Document toDocument() {
        return new Document("userId", userId)
                .append("entryDate", entryDate)
                .append("exitDate", exitDate)
                .append("overstayFlag", overstayFlag);
    }

    public ObjectId getId() { return id ; }
    public ObjectId getUserId() { return userId ; }
    public String getEntryDate() { return entryDate ; }
    public String getExitDate() { return exitDate ; }
    public void setExitDate(String exitDate) { this.exitDate = exitDate ; }
    public boolean isOverstayFlag() { return overstayFlag ; }
    public void setOverstayFlag(boolean overstayFlag) { this.overstayFlag = overstayFlag ; }
}
