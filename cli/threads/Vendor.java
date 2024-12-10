package threads;

import pool.TicketPool;
import logging.Logger;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int ticketReleaseRate;
    private final int vendorId;

    public Vendor(TicketPool ticketPool, int ticketReleaseRate, int vendorId) {
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        while (true) {
            // Lock the pool to perform atomic operations
            synchronized (ticketPool) {
                // Check if tickets sold have reached the lifecycle limit
                if (ticketPool.getTotalTicketsRetrieved() >= ticketPool.getMaxTicketCapacity()) {
                    Logger.log("Vendor " + vendorId + " detected max lifecycle limit reached. Stopping.");
                    ticketPool.setVendorFinished(); // Mark this vendor as finished
                    return; // Exit the loop
                }

                // Check if tickets can be added
                if (ticketPool.getTotalTicketsAdded() < ticketPool.getMaxTicketCapacity()) {
                    String ticket = "Ticket" + "- no " + (ticketPool.getTotalTicketsAdded() + 1);
                    ticketPool.addTickets(ticket, vendorId);
                    Logger.log("Vendor " + vendorId + " added: " + ticket);
                } else {
                    Logger.log("Vendor " + vendorId + " cannot add more tickets. Max capacity reached.");
                    ticketPool.setVendorFinished(); // Mark this vendor as finished
                    return; // Exit the loop
                }
            }

            // Sleep to simulate ticket release delay
            try {
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                Logger.error("Vendor " + vendorId + " interrupted.");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}
