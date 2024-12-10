package ui;

import config.Configuration;
import logging.Logger;

import java.util.Scanner;

public class CommandLineInterface {
    public static Configuration configureSystem() {
        Scanner scanner = new Scanner(System.in);

        Logger.log("Starting system configuration...");
        int totalTickets = getValidatedInput(scanner, "Enter Total Tickets (positive integer): ", "Examples: 10, 20, 100", 1, Integer.MAX_VALUE);
        int ticketReleaseInterval = getValidatedInput(scanner, "Enter Ticket Release Interval (milliseconds, positive integer): ", "Examples: 1000, 2000", 1, Integer.MAX_VALUE);
        int customerRetrievalInterval = getValidatedInput(scanner, "Enter Customer Retrieval Interval (milliseconds, positive integer): ", "Examples: 1000, 5000", 1, Integer.MAX_VALUE);
        int maxTicketCapacity = getValidatedInput(scanner, "Enter Max Ticket Capacity (greater than 0 and at least equal to Total Tickets): ", "Examples: 50, 100", totalTickets, Integer.MAX_VALUE);

        Logger.log("System configured successfully.");
        return new Configuration(totalTickets, ticketReleaseInterval, customerRetrievalInterval, maxTicketCapacity);
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
