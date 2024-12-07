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
        Logger.log("Enter Number of Vendors: ");
        int vendorCount = Integer.parseInt(scanner.nextLine());

        TicketPool ticketPool = new TicketPool(config.getTotalTickets(), config.getMaxTicketCapacity(), vendorCount);

        while (true) {
            Logger.log("Enter command (start/stop): ");
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("stop")) {
                Logger.log("System stopped by user command.");
                break;
            } else if (command.equalsIgnoreCase("start")) {
                runSystem(ticketPool, config, vendorCount);
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
            config.setMaxTicketCapacity(config.getMaxTicketCapacity() + newMaxTicketCapacity);

            // Update the ticket pool for the new capacity
            ticketPool.updateMaxTicketCapacity(newMaxTicketCapacity);
        }

        Logger.log("System fully terminated.");
    }

    private static void runSystem(TicketPool ticketPool, Configuration config, int vendorCount) {
        List<Thread> vendorThreads = new ArrayList<>();
        for (int i = 1; i <= vendorCount; i++) {
            vendorThreads.add(new Thread(new Vendor(ticketPool, config.getTicketReleaseRate(), i)));
        }

        Thread customer = new Thread(new Customer(ticketPool));


        // Start all threads
        vendorThreads.forEach(Thread::start);
        customer.start();

        // Wait for threads to complete
        try {
            for (Thread vendorThread : vendorThreads) {
                vendorThread.join();
            }
            customer.join();
        } catch (InterruptedException e) {
            Logger.error("Main thread interrupted.");
            Thread.currentThread().interrupt();
        }

        Logger.log("System terminated for current run.");
    }
}
