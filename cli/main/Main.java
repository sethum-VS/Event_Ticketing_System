package main;

import config.Configuration;
import pool.TicketPool;
import logging.Logger;
import threads.Customer;
import threads.Vendor;
import ui.CommandLineInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Configure the system
        Configuration config = CommandLineInterface.configureSystem();
        int vendorCount = getValidatedInput(scanner, "Enter Number of Vendors (positive integer): ", "Examples: 1, 2, 10", 1, Integer.MAX_VALUE);
        int customerCount = getValidatedInput(scanner, "Enter Number of Customers (positive integer): ", "Examples: 1, 5, 20", 1, Integer.MAX_VALUE);

        TicketPool ticketPool = new TicketPool(config.getTotalTickets(), config.getMaxTicketCapacity(), vendorCount);

        while (true) {
            String command = getValidatedCommand(scanner, "Enter command (start/stop): ", "Invalid command. Please enter 'start' or 'stop'.");

            if (command.equalsIgnoreCase("stop")) {
                Logger.log("System stopped by user command.");
                break;
            } else if (command.equalsIgnoreCase("start")) {
                runSystem(ticketPool, config, vendorCount, customerCount);
            }

            // Ask if the user wants to continue
            Logger.log("Do you want to continue? (yes/no): ");
            String continueCommand = scanner.nextLine();

            if (continueCommand.equalsIgnoreCase("no")) {
                break;
            } else if (continueCommand.equalsIgnoreCase("yes")) {
                int newMaxTicketCapacity = getValidatedInput(scanner, "Enter new Max Ticket Capacity (greater than current capacity): ",
                        "Examples: 50, 100", config.getMaxTicketCapacity() + 1, Integer.MAX_VALUE);
                config.setMaxTicketCapacity(newMaxTicketCapacity);
                ticketPool.updateMaxTicketCapacity(newMaxTicketCapacity);
            } else {
                Logger.log("Invalid response. Please enter 'yes' or 'no'.");
            }
        }

        Logger.log("System fully terminated.");
    }

    private static String getValidatedCommand(Scanner scanner, String prompt, String errorMessage) {
        while (true) {
            Logger.log(prompt);
            String command = scanner.nextLine().trim();
            if (command.equalsIgnoreCase("start") || command.equalsIgnoreCase("stop")) {
                return command;
            } else {
                Logger.log(errorMessage);
            }
        }
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

    private static void runSystem(TicketPool ticketPool, Configuration config, int vendorCount, int customerCount) {
        List<Thread> vendorThreads = new ArrayList<>();
        for (int i = 1; i <= vendorCount; i++) {
            vendorThreads.add(new Thread(new Vendor(ticketPool, config.getTicketReleaseRate(), i)));
        }

        List<Thread> customerThreads = new ArrayList<>();
        for (int i = 1; i <= customerCount; i++) {
            customerThreads.add(new Thread(new Customer(ticketPool, config.getCustomerRetrievalRate(), i)));
        }

        vendorThreads.forEach(Thread::start);
        customerThreads.forEach(Thread::start);

        try {
            for (Thread vendorThread : vendorThreads) {
                vendorThread.join();
            }
            for (Thread customerThread : customerThreads) {
                customerThread.join();
            }
        } catch (InterruptedException e) {
            Logger.error("Main thread interrupted.");
            Thread.currentThread().interrupt();
        }

        Logger.log("System terminated for current run.");
    }
}
