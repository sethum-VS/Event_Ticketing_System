package ui;

import config.Configuration;
import logging.Logger;

import java.util.Scanner;

public class CommandLineInterface {
    public static Configuration configureSystem() {
        Scanner scanner = new Scanner(System.in);

        Logger.log("Starting system configuration...");
        int totalTickets = getInput(scanner, "Enter Total Tickets: ");
        int ticketReleaseInterval = getInput(scanner, "Enter Ticket Release Interval (milliseconds): ");
        int customerRetrievalInterval = getInput(scanner, "Enter Customer Retrieval Interval (milliseconds): ");
        int maxTicketCapacity = getInput(scanner, "Enter Max Ticket Capacity: ");

        Logger.log("System configured successfully.");
        return new Configuration(totalTickets, ticketReleaseInterval, customerRetrievalInterval, maxTicketCapacity);
    }

    private static int getInput(Scanner scanner, String prompt) {
        while (true) {
            Logger.log(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value > 0) {
                    return value;
                } else {
                    Logger.log("Value must be positive. Try again.");
                }
            } catch (NumberFormatException e) {
                Logger.log("Invalid input. Please enter a number.");
            }
        }
    }
}
