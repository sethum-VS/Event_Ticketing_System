package ui;

import config.Configuration;
import logging.Logger;
import db.MongoDBUtility;
import org.bson.Document;

import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    public static Configuration configureSystem() {
        Scanner scanner = new Scanner(System.in);
        MongoDBUtility dbUtility = new MongoDBUtility();

        Logger.log("Do you want to load a saved configuration? (yes/no): ");
        String choice = scanner.nextLine().trim();

        if (choice.equalsIgnoreCase("yes")) {
            List<Document> configurations = dbUtility.getAllConfigurations();
            if (configurations.isEmpty()) {
                Logger.log("No saved configurations found. Proceeding with new configuration.");
            } else {
                Logger.log("Saved Configurations:");
                for (int i = 0; i < configurations.size(); i++) {
                    Document config = configurations.get(i);
                    String createdAt = config.getString("createdAt");
                    String id = config.getObjectId("_id").toString();
                    Logger.log((i + 1) + ". Date: " + createdAt + ", Config ID: " + id);
                }

                int selectedConfig = getValidatedInput(scanner, "Select a configuration by number: ", "Examples: 1, 2", 1, configurations.size());
                Document selectedDocument = configurations.get(selectedConfig - 1);
                Logger.log("Loading configuration...");
                Configuration loadedConfig = new Configuration(
                        selectedDocument.getInteger("totalTickets"),
                        selectedDocument.getInteger("ticketReleaseRate"),
                        selectedDocument.getInteger("customerRetrievalRate"),
                        selectedDocument.getInteger("maxTicketCapacity")
                );
                Logger.log("Configuration loaded successfully.");
                return loadedConfig;
            }
        }

        Logger.log("Starting system configuration...");
        int totalTickets = getValidatedInput(scanner, "Enter Total Tickets (positive integer): ", "Examples: 10, 20, 100", 1, Integer.MAX_VALUE);
        int ticketReleaseInterval = getValidatedInput(scanner, "Enter Ticket Release Interval (milliseconds, positive integer): ", "Examples: 1000, 2000", 1, Integer.MAX_VALUE);
        int customerRetrievalInterval = getValidatedInput(scanner, "Enter Customer Retrieval Interval (milliseconds, positive integer): ", "Examples: 1000, 5000", 1, Integer.MAX_VALUE);
        int maxTicketCapacity = getValidatedInput(scanner, "Enter Max Ticket Capacity (greater than 0 and at least equal to Total Tickets): ", "Examples: 50, 100", totalTickets, Integer.MAX_VALUE);

        Configuration config = new Configuration(totalTickets, ticketReleaseInterval, customerRetrievalInterval, maxTicketCapacity);
        String configId = dbUtility.saveConfiguration(config);
        if (configId != null) {
            Logger.log("Configuration saved with ID: " + configId);
        }
        Logger.log("System configured successfully.");
        return config;
    }


    private static int getValidatedInput(Scanner scanner, String prompt, String examples, int minValue, int maxValue) {
        while (true) {
            Logger.log(prompt + " " + examples);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= minValue && value <= maxValue) {
                    return value;
                } else {
                    Logger.log("Input must be between " + minValue + " and " + maxValue + ". Try again.");
                }
            } catch (NumberFormatException e) {
                Logger.log("Invalid input. Please enter a valid number. " + examples);
            }
        }
    }
}
