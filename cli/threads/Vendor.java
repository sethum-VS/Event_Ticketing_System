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
        int ticketNumber = 1;
        while (ticketPool.getTotalTicketsAdded() < ticketPool.getMaxTicketCapacity()) {
            for (int i = 0; i < ticketReleaseRate; i++) {
                if (ticketPool.getTotalTicketsAdded() >= ticketPool.getMaxTicketCapacity()) {
                    Logger.log("Vendor " + vendorId + " finished adding tickets.");
                    ticketPool.setVendorFinished();
                    return;
                }

                String ticket = "Ticket-" + ticketNumber++;
                ticketPool.addTickets(ticket, vendorId);
                Logger.log("Vendor " + vendorId + " added: " + ticket);

                try {
                    Thread.sleep(500); // Simulate delay in ticket release
                } catch (InterruptedException e) {
                    Logger.error("Vendor " + vendorId + " interrupted.");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        Logger.log("Vendor " + vendorId + " finished adding tickets.");
        ticketPool.setVendorFinished(); // Signal end of ticket addition
    }
}
