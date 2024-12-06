package threads;

import pool.TicketPool;
import logging.Logger;

public class Customer implements Runnable {
    private final TicketPool ticketPool;

    public Customer(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        while (true) {
            String ticket = ticketPool.removeTicket();

            if (ticket != null) {
                Logger.log("Customer retrieved: " + ticket);
            } else if (ticketPool.isComplete()) {
                Logger.log("Customer finished retrieving tickets. No more tickets available.");
                break;
            }

            try {
                Thread.sleep(500); // Simulate delay in ticket retrieval
            } catch (InterruptedException e) {
                Logger.error("Customer interrupted.");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
