package main;

import config.Configuration;
import pool.TicketPool;
import logging.Logger;
import threads.Customer;
import threads.Vendor;
import ui.CommandLineInterface;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Configure the system
        Configuration config = CommandLineInterface.configureSystem();
        TicketPool ticketPool = new TicketPool(config.getTotalTickets(), config.getMaxTicketCapacity());

        while (true) {
            Logger.log("Enter command (start/stop): ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("stop")) {
                Logger.log("System stopped by user command.");
                break;
            } else if (command.equalsIgnoreCase("start")) {
                runSystem(ticketPool, config);
            } else {
                Logger.log("Invalid command. Please enter 'start' or 'stop'.");
            }

            // Ask if the user wants to continue
            Logger.log("Do you want to continue? (yes/no): ");
            String continueCommand = scanner.nextLine();

            if (continueCommand.equalsIgnoreCase("no")) {
                break;
            }

            // Update Max Ticket Capacity
            Logger.log("Enter new Max Ticket Capacity: ");
            int newMaxTicketCapacity = Integer.parseInt(scanner.nextLine());

            // Treat the new capacity as an increment to the existing capacity
            int incrementCapacity = newMaxTicketCapacity;
            config.setMaxTicketCapacity(config.getMaxTicketCapacity() + incrementCapacity);

            // Update the ticket pool for the new capacity
            ticketPool.updateMaxTicketCapacity(config.getMaxTicketCapacity());
        }

        Logger.log("System fully terminated.");
    }

    private static void runSystem(TicketPool ticketPool, Configuration config) {
        // Initialize Vendor and Customer threads
        Thread vendor = new Thread(new Vendor(ticketPool, config.getTicketReleaseRate()));
        Thread customer = new Thread(new Customer(ticketPool));

        // Start threads
        vendor.start();
        customer.start();

        // Wait for threads to complete
        try {
            vendor.join();
            customer.join();
        } catch (InterruptedException e) {
            Logger.error("Main thread interrupted.");
            Thread.currentThread().interrupt();
        }

        Logger.log("System terminated for current run.");
    }
}
