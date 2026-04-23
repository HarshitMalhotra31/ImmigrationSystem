package com.immigration.dao;

import com.immigration.model.EntryExit;
import com.immigration.util.DBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class EntryExitDAO {
    private final MongoCollection<Document> collection;

    public EntryExitDAO() {
        this.collection = DBConnection.getDatabase().getCollection("entry_exit");
    }

    public void save(EntryExit entryExit) {
        collection.insertOne(entryExit.toDocument());
    }

    // Returns the LATEST entry/exit record for the user (sorted by _id descending)
    public EntryExit findByUserId(ObjectId userId) {
        Document doc = collection.find(Filters.eq("userId", userId))
                .sort(Sorts.descending("_id"))
                .first();
        return (doc != null) ? new EntryExit(doc) : null;
    }

    // Updates only the active record (the one with no exitDate) for this user
    public void updateExit(ObjectId userId, String exitDate, boolean overstay) {
        collection.updateOne(
                Filters.and(Filters.eq("userId", userId), Filters.eq("exitDate", null)),
                new Document("$set", new Document("exitDate", exitDate).append("overstayFlag", overstay)));
    }

    public List<EntryExit> getAllRecords() {
        List<EntryExit> records = new ArrayList<>();
        for (Document doc : collection.find()) {
            records.add(new EntryExit(doc));
        }
        return records;
    }
}
