package threads;

import pool.TicketPool;
import logging.Logger;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int ticketReleaseRate;

    public Vendor(TicketPool ticketPool, int ticketReleaseRate) {
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
    }

    @Override
    public void run() {
        while (ticketPool.getTotalTicketsAdded() < ticketPool.getMaxTicketCapacity()) {
            while (ticketPool.getCurrentPoolSize() < ticketPool.getTotalTickets() &&
                    ticketPool.getTotalTicketsAdded() < ticketPool.getMaxTicketCapacity()) {
                for (int i = 0; i < ticketReleaseRate; i++) {
                    if (ticketPool.getTotalTicketsAdded() >= ticketPool.getMaxTicketCapacity()) {
                        Logger.log("Vendor: Max ticket capacity reached. Stopping ticket release.");
                        ticketPool.setVendorFinished();
                        return;
                    }

                    String ticket = "Ticket-" + System.nanoTime();
                    ticketPool.addTickets(ticket);
                    Logger.log("Vendor added: " + ticket);

                    try {
                        Thread.sleep(500); // Simulate delay in ticket release
                    } catch (InterruptedException e) {
                        Logger.error("Vendor interrupted.");
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            try {
                Thread.sleep(100); // Prevent busy waiting
            } catch (InterruptedException e) {
                Logger.error("Vendor interrupted during wait.");
                Thread.currentThread().interrupt();
                return;
            }
        }

        Logger.log("Vendor finished releasing tickets.");
        ticketPool.setVendorFinished(); // Notify that no more tickets will be added
    }
}
