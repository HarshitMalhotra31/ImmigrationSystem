package com.immigration.dao;

import com.immigration.model.Visa;
import com.immigration.util.DBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class VisaDAO {
    private final MongoCollection<Document> collection;

    public VisaDAO() {
        this.collection = DBConnection.getDatabase().getCollection("visas");
    }

    public void save(Visa visa) {
        collection.insertOne(visa.toDocument());
    }

    public Visa findByUserId(ObjectId userId) {
        Document doc = collection.find(Filters.eq("userId", userId)).first();
        return (doc != null) ? new Visa(doc) : null;
    }

    public List<Visa> getAllVisas() {
        List<Visa> visas = new ArrayList<>();
        for (Document doc : collection.find()) {
            visas.add(new Visa(doc));
        }
        return visas;
    }

    public void updateStatus(ObjectId id, String status) {
        collection.updateOne(Filters.eq("_id", id), new Document("$set", new Document("approvalStatus", status)));
    }
}
