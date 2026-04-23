package com.immigration.dao;

import com.immigration.model.User;
import com.immigration.util.DBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final MongoCollection<Document> collection;

    public UserDAO() {
        this.collection = DBConnection.getDatabase().getCollection("users");
    }

    public void save(User user) {
        collection.insertOne(user.toDocument());
    }

    public User findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return (doc != null) ? new User(doc) : null;
    }

    public User findByUsername(String username) {
        Document doc = collection.find(Filters.eq("username", username)).first();
        return (doc != null) ? new User(doc) : null;
    }

    public User findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return (doc != null) ? new User(doc) : null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (Document doc : collection.find()) {
            users.add(new User(doc));
        }
        return users;
    }

    public User findByPassportNumber(String passportNumber) {
        Document doc = collection.find(Filters.eq("passportNumber", passportNumber)).first();
        return (doc != null) ? new User(doc) : null;
    }

    public void updateRiskScore(ObjectId id, int score) {
        collection.updateOne(Filters.eq("_id", id), new Document("$set", new Document("riskScore", score)));
    }

    public void updateStatus(ObjectId id, String status) {
        collection.updateOne(Filters.eq("_id", id), new Document("$set", new Document("status", status)));
    }
}
