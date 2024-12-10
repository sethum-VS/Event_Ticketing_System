package db;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.Configuration;
import logging.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MongoDBUtility {

    private static final String CONNECTION_STRING = "mongodb+srv://SMW:1234@cluster0.ihotw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static final String DATABASE_NAME = "ticketing_system";
    private static final String COLLECTION_NAME = "configurations";

    private MongoCollection<Document> configurationCollection;

    public MongoDBUtility() {
        try {
            var mongoClient = MongoClients.create(CONNECTION_STRING);
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            configurationCollection = database.getCollection(COLLECTION_NAME);
            Logger.log("Connected to MongoDB successfully.");
        } catch (Exception e) {
            Logger.error("Failed to connect to MongoDB: " + e.getMessage());
        }
    }

    public String saveConfiguration(Configuration config) {
        try {
            Document document = new Document()
                    .append("totalTickets", config.getTotalTickets())
                    .append("ticketReleaseRate", config.getTicketReleaseRate())
                    .append("customerRetrievalRate", config.getCustomerRetrievalRate())
                    .append("maxTicketCapacity", config.getMaxTicketCapacity())
                    .append("createdAt", LocalDateTime.now().toString());
            configurationCollection.insertOne(document);
            return document.getObjectId("_id").toString();
        } catch (Exception e) {
            Logger.error("Error saving configuration: " + e.getMessage());
            return null;
        }
    }

    public Configuration getConfigurationById(String id) {
        try {
            Document document = configurationCollection.find(new Document("_id", new ObjectId(id))).first();
            if (document == null) {
                Logger.log("Configuration not found for ID: " + id);
                return null;
            }
            return new Configuration(
                    document.getInteger("totalTickets"),
                    document.getInteger("ticketReleaseRate"),
                    document.getInteger("customerRetrievalRate"),
                    document.getInteger("maxTicketCapacity")
            );
        } catch (Exception e) {
            Logger.error("Error retrieving configuration: " + e.getMessage());
            return null;
        }
    }

    public List<Document> getAllConfigurations() {
        try {
            return configurationCollection.find().into(new ArrayList<>());
        } catch (Exception e) {
            Logger.error("Error retrieving configurations: " + e.getMessage());
            return Collections.emptyList();
        }
    }

}
